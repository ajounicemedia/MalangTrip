package com.example.malangtrip.nav.community.writecommunity

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.malangtrip.key.GetTime
import com.example.malangtrip.key.CommunityItem

import com.example.malangtrip.nav.community.readcommunity.GoToBoard
import com.example.malangtrip.databinding.ActivityFixBoardBinding
import com.example.malangtrip.key.DBKey
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class FixBoard : AppCompatActivity() {
    private lateinit var binding : ActivityFixBoardBinding


    private var photoCheck = false
    //이미지 여러개일 때 사용
//    private val imageUrls = mutableListOf<String>()
//    private var imageCount = 1
        //private lateinit var name:String
//    private lateinit var content:String
//    private lateinit var title:String
    private lateinit var key:String
    private lateinit var name:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFixBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)



        key = intent.getStringExtra("key").toString()
        name  = intent.getStringExtra("name").toString()

        Log.d("키값똑같노",key)

        getBoardData(key)
        getImageData(key)
        //이미지 여러개일 때
//        imageloadAdapter = Board_Image_Adapter(imageUrls)
//        binding.imageList.adapter = imageloadAdapter
        //binding.imageList.layoutManager = LinearLayoutManager(this)

        binding.btnInput.setOnClickListener {
            photoCheck()
        }



        binding.btnImgChoice.setOnClickListener {
            imageChoice()
        }


    }
    private fun photoCheck()
    {
        if(photoCheck) {
            uploadImage(key)
            editBoardDataWithDelay(key)
        }
        else {
            editBoardData(key)
        }
    }
    private fun imageChoice()
    {
        val gallery = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        }
        binding.ivArea.isVisible = true
        startActivityForResult(gallery, 100)
    }
    //이미지 단일일 때
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK && requestCode ==100)
        {
            photoCheck = true
            binding.ivArea.setImageURI( data?.data)
        }
    }

    private fun uploadImage(key : String){

        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child(key + ".png")

        val imageView = binding.ivArea
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

    private fun getImageData(key : String)
    {
        /// Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference.child(key + ".png")

        // ImageView in your Activity
        val imageViewFromFB = binding.ivArea
        Log.d("Fix_Board", "Trying to get image from Firebase Storage...")
        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if(task.isSuccessful) {
                Log.d("Fix_Board", "Image download successful!")
                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)

            } else {
                binding.ivArea.isVisible = false
                Log.w("Fix_Board", task.exception?.message ?: "Image load failed.")
            }
        })
    }
    private fun editBoardData(key : String){
        //사진 여러개일 때
        val title = binding.etTitle.text.toString()
        val content = binding.etContent.text.toString()
        val time = GetTime.getTime()
        val curruntId = Firebase.auth.currentUser?.uid ?: "" // 현재 유저 아이디 가져오기

        Firebase.database.reference.child(DBKey.Community_Key).child(key).child("boardType")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val boardType = dataSnapshot.getValue(String::class.java)



                    if (boardType == "free") {
                        Firebase.database(DBKey.DB_URL).reference.child(DBKey.Community_Key).child(key)
                            .setValue(CommunityItem(curruntId,name, title, content, time,"free"))
                    } else {
                        Firebase.database(DBKey.DB_URL).reference.child(DBKey.Community_Key).child(key)
                            .setValue(CommunityItem(curruntId,name, title, content, time,"passenger"))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 데이터 읽기가 실패했을 때 실행됩니다.
                    Log.w(TAG, "Failed to read value.", databaseError.toException())
                }
            })


        finish()
        val intent = Intent(this, GoToBoard::class.java)
        intent.putExtra("key", key)
        intent.putExtra("name",name)
        startActivity(intent)
    }
    private fun editBoardDataWithDelay(key: String) {
        // 1.5초 딜레이를 주기 위해 Handler를 사용합니다.
        finish()
        Handler().postDelayed({
            editBoardData(key)
        }, 1400)
    }

    private fun getBoardData(key : String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataModel = dataSnapshot.getValue(CommunityItem::class.java)
                binding.etTitle.setText(dataModel!!.title)
                binding.etContent.setText(dataModel!!.content)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        Firebase.database(DBKey.DB_URL).reference.child(DBKey.Community_Key).child(key).addValueEventListener(postListener)

    }
}