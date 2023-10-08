package com.example.malangtrip.nav.myinfo.driver

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.malangtrip.key.UserInfo
import com.example.malangtrip.key.DriverInfo
import com.example.malangtrip.R
import com.example.malangtrip.databinding.FragmentRegisterDriverBinding
import com.example.malangtrip.key.DBKey
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

//드라이버로 등록하기
class ResisterDriver : Fragment(){
    private var _binding: FragmentRegisterDriverBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.

    private val binding get() = _binding!!
    private var photoCheck = false
    //private lateinit var radioGroup: RadioGroup
    private var checkedRadioButtonId: Int = -1
    private var driverLocal:String?=null
    private var name: String? = null
    private lateinit var description:String
    val curruntId = Firebase.auth.currentUser?.uid ?: "" // 현재 유저 아이디 가져오기
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentRegisterDriverBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val myDb = Firebase.database.reference.child(DBKey.DB_USERS).child(curruntId)//내 정보 접근
        myDb.get().addOnSuccessListener {
            val myInfo = it.getValue(UserInfo::class.java)?: return@addOnSuccessListener
            name = myInfo.nickname.toString()
            description = myInfo.description.toString()
        }
        //액션바 활성화 및 이름 변경후 뒤로가기 버튼 활성화
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "드라이버로 등록하기"
        //메뉴 사용 활성화
        setHasOptionsMenu(true)

        // 뒤로가기 버튼 처리 이전 프래그먼트로 감
        root.isFocusableInTouchMode = true
        root.requestFocus()
        root.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                requireActivity().supportFragmentManager.popBackStack()
                true
            } else {
                false
            }
        }
        //사진 선택
        binding.ivUpload.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
        }
        binding.btnCancle.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnComplete.setOnClickListener {
            complete()
        }
        binding.btnSelectLocal.setOnClickListener {
            showDialog()
        }


        return root
    }
    private fun complete()
    {
        val carType = binding.etCarType.text.toString().trim()
        val licensePlate = binding.etCarNum.text.toString().trim()
        val availableNum = binding.etAvailableNum.text.toString().trim()

        if (carType.isEmpty() || licensePlate.isEmpty() || availableNum.isEmpty() || driverLocal == null) {
            Toast.makeText(context,"입력 안 된 정보가 있습니다.", Toast.LENGTH_SHORT).show()
        } else {
            // 나머지 코드들을 이곳에 배치하세요.


            if (photoCheck == true) {
                uploadImage(curruntId)
            }
            if (driverLocal == "jeju") {
                Firebase.database(DBKey.DB_URL).reference.child(DBKey.Driver)
                    .child(curruntId)
                    .setValue(
                        DriverInfo(
                            "jeju",
                            name,
                            description,
                            carType,
                            licensePlate,
                            availableNum,
                            null
                        )
                    )
            }
            if (driverLocal == "ulleung") {
                Firebase.database(DBKey.DB_URL).reference.child(DBKey.Driver)
                    .child(curruntId)
                    .setValue(
                        DriverInfo(
                            "ulleung",
                            name,
                            description,
                            carType,
                            licensePlate,
                            availableNum
                        )
                    )
            }
            Toast.makeText(context,"빠른 시일내에 드라이버 등록 심사가 될 예정입니다 감사합니다.", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== AppCompatActivity.RESULT_OK && requestCode ==100)
        {
            photoCheck = true
            binding.ivUpload.setImageURI( data?.data)
        }
    }
    private fun uploadImage(key : String){

        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child(key + ".png")

        val imageView = binding.ivUpload
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }
    //상단 뒤로가기 버튼 눌렀을 때 이전 프래그먼트로
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                //requireActivity().onBackPressed()
                requireActivity().supportFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showDialog()
    {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_local_choice,null)
        val builder = context?.let {
            AlertDialog.Builder(it)
                .setView(dialogView)
                .setTitle("지역선택")
        }

        val alterDialog = builder?.show()
        val radioGroup = alterDialog?.findViewById<RadioGroup>(R.id.rg_local)
        radioGroup?.clearCheck()

        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            checkedRadioButtonId = checkedId
        }
        alterDialog?.findViewById<Button>(R.id.btn_cancle)?.setOnClickListener {

            alterDialog.dismiss()

        }
        alterDialog?.findViewById<Button>(R.id.btn_check)?.setOnClickListener {
            when (checkedRadioButtonId) {
                R.id.rb_jeju -> {
                    driverLocal = "jeju"
                }
                R.id.rb_ulleung -> {
                    driverLocal = "ulleung"
                }

            }
            driverLocal?.let { it1 -> Log.d("지역", it1) }
            //Log.d("지역",driverLocal)
            alterDialog.dismiss()
        }
    }
}