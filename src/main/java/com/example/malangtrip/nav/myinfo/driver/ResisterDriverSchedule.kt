package com.example.malangtrip.nav.myinfo.driver

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.malangtrip.databinding.FragmentRegisterDriverTripBinding
import com.example.malangtrip.key.DriverInfo
import com.example.malangtrip.key.TripInfo
import com.example.malangtrip.key.DBKey
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ResisterDriverSchedule : Fragment() {
    private var _binding: FragmentRegisterDriverTripBinding? = null
    private val binding get() = _binding!!
    var local = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentRegisterDriverTripBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //액션바 활성화 및 이름 변경후 뒤로가기 버튼 활성화
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "여행 정보 등록하기"
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

        binding.btnResisterTrip.setOnClickListener {
            restisterTrip()
        }
        return root
    }
    private fun restisterTrip()
    {
        val title = binding.etTripTitle.text.toString().trim()
        val schedule = binding.etTripSchedule.text.toString().trim()
        val content = binding.etTripContent.text.toString().trim()
        val price = binding.etTripPrice.text.toString().trim()

        if(title.isEmpty()||schedule.isEmpty()||content.isEmpty()||price.isEmpty())
        {
            Toast.makeText(context,"입력 안 된 정보가 있습니다.", Toast.LENGTH_SHORT).show()
        }
        else{
            val curruntId = Firebase.auth.currentUser?.uid ?: "" // 현재 유저 아이디 가져오기
            val mydb = Firebase.database.reference.child(DBKey.Driver).child(curruntId)//내 정보 접근
            val key =  Firebase.database(DBKey.DB_URL).reference.push().key.toString()
            mydb.get().addOnSuccessListener {
                val myInfo = it.getValue(DriverInfo::class.java)?: return@addOnSuccessListener
                local = myInfo.local.toString()
                Log.d("sdfsd",local)
                Firebase.database(DBKey.DB_URL).reference.child(DBKey.Trip_Info)
                    .child(curruntId).child(key)
                    .setValue(
                        TripInfo(
                            curruntId,key,local,title,schedule, content,price
                        )
                    )
            }

        }
        requireActivity().supportFragmentManager.popBackStack()
    }
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

}