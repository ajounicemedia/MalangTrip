package com.example.malangtrip.nav.community.writecommunity


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.malangtrip.key.UserInfo
import com.example.malangtrip.key.GetTime
import com.example.malangtrip.key.CommunityItem
import com.example.malangtrip.R
import com.example.malangtrip.databinding.ActivityWriteTextBinding
import com.example.malangtrip.key.DBKey
import com.example.malangtrip.key.DBKey.Companion.Community_Key
import com.example.malangtrip.key.DBKey.Companion.DB_URL
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class WriteText : AppCompatActivity(){
    private lateinit var binding: ActivityWriteTextBinding
    //private lateinit var imageAdapter: ImageAdapter
    private lateinit var key: String
    private var photoCheck : Boolean = false
    var selectedRadioButtonId = R.id.rb_free// Default value
    //private var imageCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteTextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = "글쓰기"
            setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 표시
            supportActionBar?.setHomeAsUpIndicator(R.drawable.my_home_back)
        }


        binding.rgChoiceBoard.setOnCheckedChangeListener { group, checkedId ->
            selectedRadioButtonId = checkedId
        }
        binding.btnInput.setOnClickListener {
            inputData()
        }
        // 사진 한장만 선택할 때
        binding.btnImgChoice.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
           startActivityForResult(gallery, 100)
        }
        //사진 여러장 선택할 때
//        binding.imageChoiceBtn.setOnClickListener {
//            val gallery = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//                addCategory(Intent.CATEGORY_OPENABLE)
//                type = "image/*"
//                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//
//            }
//            photo_Check = true
//            startActivityForResult(gallery, 100)
//
//        }

    }
    private fun inputData()
    {
        val title = binding.evTitle.text.toString()
        val content = binding.evContent.text.toString()
        val uid = Firebase.auth.currentUser?.uid ?: ""
        val time = GetTime.getTime()

        val curruntId = Firebase.auth.currentUser?.uid ?: "" // 현재 유저 아이디 가져오기
        val myDb = Firebase.database.reference.child(DBKey.DB_USERS).child(curruntId)//내 정보 접근
        key =  Firebase.database(DB_URL).reference.push().key.toString()


        myDb.get().addOnSuccessListener {
            val myInfo = it.getValue(UserInfo::class.java) ?: return@addOnSuccessListener
            val myName = myInfo.nickname.toString()



            if (content.trim().isNotEmpty() && title.trim().isNotEmpty()) {

                when (selectedRadioButtonId) {
                    R.id.rb_free -> {
                        Firebase.database(DB_URL).reference.child(Community_Key).child(key)
                            .setValue(CommunityItem(key,uid,myName, title, content, time,"free"))
                    }
                    R.id.rb_passanger -> {
                        Firebase.database(DB_URL).reference.child(Community_Key).child(key)
                            .setValue(CommunityItem(key,uid,myName, title, content, time,"passenger"))
                    }
                }
                if(photoCheck==true)
                {
                    Log.d("!2323","이미지 업로드완료")
                    upload_Image(key)
                }



                finish()

            } else {
                Toast.makeText(this, "내용이나 제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

            //사진 한장만 선택 할 때
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK && requestCode ==100)
        {
            photoCheck = true
            binding.imgArea.setImageURI( data?.data)
        }
    }
            //사진 여러장 선택할 때
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//                if (resultCode == RESULT_OK && requestCode == 100) {
//                    val imageUris = mutableListOf<Uri>()
//                    data?.clipData?.let { clipData ->
//                        for (i in 0 until clipData.itemCount) {
//                            imageUris.add(clipData.getItemAt(i).uri)
//                        }
//                    } ?: data?.data?.let { singleImageUri ->
//                        imageUris.add(singleImageUri)
//                    }
//
//                    imageAdapter = ImageAdapter(imageUris)
//                    binding.imageList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//                    binding.imageList.adapter = imageAdapter
//
//                }
//    }

    //사진 여러장 선택 할 때
//    private fun upload_Image(key : String)
//    {
//        val storage = Firebase.storage
//        val imageUris = imageAdapter.getImageUris()
//        imageCount = imageUris.size
//        //intent.putExtra("imageCount",imageUris.size)
//        val storageRef = storage.reference
//        for ((index, uri) in imageUris.withIndex()) { // Here, we get the index with the Uri
//            val fileName = "$key-$index.png" // We append the index to the key for unique names
//            val mountainsRef = storageRef.child(fileName)
//            Glide.with(this)
//                .asBitmap()
//                .load(uri)
//                .into(object : CustomTarget<Bitmap>() {
//                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                        val baos = ByteArrayOutputStream()
//                        resource.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//                        val data = baos.toByteArray()
//
//
//                        var uploadTask = mountainsRef.putBytes(data)
//                        uploadTask.addOnFailureListener {
//                            // Handle unsuccessful uploads
//                        }.addOnSuccessListener { taskSnapshot ->
//                            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
//                            // ...
//                        }
//                    }
//
//                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {
//                        // this is called when the bitmap is cleared/collected
//                    }
//                })
//        }
//    }
    private fun upload_Image(key : String){

        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child(key+".png")

        val imageView = binding.imgArea
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                //requireActivity().onBackPressed()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}