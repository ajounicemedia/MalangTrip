package com.example.malangtrip.login

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.example.malangtrip.key.DBKey
import com.example.malangtrip.databinding.ActivityInputUserDataBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

//데이터 입력 받기
class UserDataInput : AppCompatActivity() {
    private lateinit var binding: ActivityInputUserDataBinding

    private var isImageUpload = false
    private val curruntId = Firebase.auth.currentUser?.uid ?: ""// 현재 유저 아이디 가져오기
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputUserDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide() // 액션바 숨김

        if (curruntId == null) {
            startActivity(Intent(this, EmailLogin::class.java))
            finish()
        }

        //프사 선택
        binding.ivProfilePhoto.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
            isImageUpload = true
        }

        //홈으로 가기
        binding.btnGoHome.setOnClickListener {
            uploadData()
        }


    }

    private fun imageUpload(key: String) {

        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child(key + ".png")
        val imageView = binding.ivProfilePhoto

        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {

        }.addOnSuccessListener { taskSnapshot ->

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            binding.ivProfilePhoto.setImageURI(data?.data)
        }

    }

    private fun uploadData() {
        if (isImageUpload == true) {
            imageUpload(curruntId)
        }
        val nickname = binding.etNickname.text.toString()
        val description = binding.etDes.text.toString()
        //법인 설립후 사용할 기능
        //  val bank = binding.InputBank.text.toString()
        // val bank_Num = binding.InputBankNum.text.toString()
        val currentUser = Firebase.auth.currentUser
        val email = currentUser?.email.toString()


        //주석은 법인 설립후
        if (nickname.isEmpty()/*||bank.isEmpty()||bank_Num.isEmpty()*/) {
            Toast.makeText(this, "닉네임은 빈 값으로 둘 수 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

            val mydb = Firebase.database.reference.child(DBKey.DB_USERS).child(curruntId)//내 정보 접근
            val myprofile = mutableMapOf<String, Any>()
            myprofile["email"] = email
            myprofile["nickname"] = nickname
            myprofile["description"] = description
            //법인 설립후 사용할 기능
//            myprofile["bankNum"] = bank_Num
//            myprofile["bank"] =  bank
            mydb.updateChildren(myprofile)
            startActivity(Intent(this, CompleteJoin::class.java))
            finish()
        }

}