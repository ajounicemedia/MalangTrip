package com.example.malangtrip.nav.community.readcommunity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.malangtrip.key.CommentItem
import com.example.malangtrip.key.GetTime
import com.example.malangtrip.key.CommunityItem
import com.example.malangtrip.nav.community.comment.CommentAdapter
import com.example.malangtrip.nav.community.writecommunity.FixBoard
import com.example.malangtrip.R
import com.example.malangtrip.databinding.ActivityBoardInsideBinding
import com.example.malangtrip.key.DBKey
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class GoToBoard : AppCompatActivity(){
    private lateinit var binding: ActivityBoardInsideBinding


    private lateinit var key: String
    var menuCheck = false
    //사진 여러장
    //private lateinit var imageAdapter: Board_Image_Adapter
//    private var imageCount by Delegates.notNull<Int>()
//    private val imageUrls = mutableListOf<String>()
    private  lateinit var name :String
    private val commentDataList = mutableListOf<CommentItem>()
    private lateinit var commentAdapter: CommentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityBoardInsideBinding.inflate(layoutInflater)
        setContentView(binding.root)


        key = intent.getStringExtra("key").toString()
        name  = intent.getStringExtra("name").toString()
        
    //사진 여러장
//        imageAdapter = Board_Image_Adapter(imageUrls)
//        binding.imageItems.adapter = imageAdapter
//        binding.imageItems.layoutManager = LinearLayoutManager(this)

        //글쓰기 데이터 가져오기
        getBoardData(key)
        //이미지 가져오기
        getImageData(key)
        //댓글 등록하기
        binding.btnRegisterComment.setOnClickListener {
            registerComment()
        }
       // getCommentData(key)

        //리스트뷰
        //댓글 생성
        createComment()
        //리사이클러뷰
//        commentAdapter = Comment_Adapter(commentDataList)
//        binding.commentLV.layoutManager = LinearLayoutManager(this)
//        binding.commentLV.adapter = commentAdapter
        //키보드 나타났을 때 채팅창 위로 올림



    }
    private fun createComment()
    {
        commentAdapter = CommentAdapter(commentDataList)
        binding.lvComment.adapter = commentAdapter
        getCommentData(key)
    }
    private fun registerComment()
    {
        val comment = binding.etComment.text.toString()
        if(comment==null)
        {
            Toast.makeText(this, "아무 것도 입력하지 않으셨습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        insertComment(key)
        Toast.makeText(this, "댓글 입력 완료", Toast.LENGTH_SHORT).show()
//            binding.commentLV.post {
//                binding.commentLV.setSelection(commentAdapter.count - 1)
//            }
        // 키보드 내리기
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etComment.windowToken, 0)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.svDown.fullScroll(View.FOCUS_DOWN)
        }, 300)
    }

    //사진 여러장
//   private fun getImageData(key : String)
//    {
//        val storageReference = Firebase.storage.reference
//         imageCount = intent.getIntExtra("imageCount", 0) // Assuming you pass the image count from the previous activity
//
//        for (i in 0 until imageCount) {
//            val fileName = "$key-$i.png"
//            val imageRef = storageReference.child(fileName)
//            imageRef.downloadUrl.addOnSuccessListener { uri ->
//                imageUrls.add(uri.toString())
//                imageAdapter.notifyDataSetChanged()
//
//            }.addOnFailureListener {
//                // Handle any errors
//            }
//        }
//
//
//    }
    //단일 사진
private fun getImageData(key : String)
{
    /// Reference to an image file in Cloud Storage
    val storageReference = Firebase.storage.reference.child(key + ".png")

    // ImageView in your Activity
    val imageViewFromFB = binding.ivBoardImg
    Log.d("123",key + ".png")
    storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
        if(task.isSuccessful) {

            Glide.with(this)
                .load(task.result)
                .into(imageViewFromFB)


        } else {
                binding.ivBoardImg.isVisible = false
        }
    })
}

    private fun insertComment(key : String){
    val boardDb = Firebase.database.reference.child(DBKey.Community_Key).child(key)
    boardDb.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val boardInfo = dataSnapshot.getValue(CommunityItem::class.java)
            val commentNum = boardInfo?.commentNum ?: 0
            boardDb.child("commentNum").setValue(commentNum + 1)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // 오류 처리
        }
    })

        val myId = Firebase.auth.currentUser?.uid ?: "" // 현재 유저 아이디 가져오기
        // comment
        //   - BoardKey
        //        - CommentKey
        //            - CommentData
        //            - CommentData
        //            - CommentData
        Firebase.database.getReference("Users").child(myId).child("nickname")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val name = dataSnapshot.getValue(String::class.java)
                    if (name != null) {
                        // 여기서 검색된 이름을 사용합니다
                        Firebase.database.getReference("Comment")
                            .child(key)
                            .push()
                            .setValue(
                                CommentItem(
                                    name,
                                    binding.etComment.text.toString(),
                                    GetTime.getTime()

                                )
                            )
                        binding.etComment.setText("")
                        getCommentData(key)
                        //binding.commentLV.setSelection(commentAdapter.count - 1)

                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 여기서 잠재적인 오류를 처리합니다
                }
            })



    }

    private fun getBoardData(key : String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {

                    val dataModel = dataSnapshot.getValue(CommunityItem::class.java)


                        //binding.boardTitle.text = dataModel!!.title
                    supportActionBar?.apply {
                        title = dataModel!!.title
                        setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 표시
                        supportActionBar?.setHomeAsUpIndicator(R.drawable.my_home_back)
                    }
                    binding.tvContent.text = dataModel!!.content
                    binding.tvWritingTime.text = dataModel!!.time
                    binding.tvWriterName.text = dataModel!!.userName
                    val myId = Firebase.auth.currentUser?.uid ?: "" // 현재 유저 아이디 가져오기
                    val textId = dataModel.userId
                    if(myId==textId)
                    {

                        Log.d("나 자신임",myId)
                        menuCheck=true
                        //invalidateOptionsMenu()
                    }
                    else{
                        Log.d("나 자신이 아님",myId)
                    }

                } catch (e: Exception) {

                    Log.d(TAG, "삭제완료")

                }





            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

    Firebase.database(DBKey.DB_URL).reference.child(DBKey.Community_Key).child(key).addValueEventListener(postListener)
    }
    //이미지 빨리 불러오려고
    override fun onResume() {
        super.onResume()
        getImageData(key)
    }
    fun getCommentData(key:String)
    {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                    commentDataList.clear()
                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(CommentItem::class.java)
                    commentDataList.add(item!!)
                }
                commentAdapter.notifyDataSetChanged()
                // 스크롤을 마지막 댓글로 이동



                updateListViewHeight()

            }


            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }


        //Firebase.database.reference.child(DBKey.DB_USERS).child(curruntId)//내 정보 접근
        Firebase.database.reference.child(DBKey.Comment_Key).child(key).addValueEventListener(postListener)


    }
    //댓글 생길 때마다 댓글 어댑터 높이 조정
    fun updateListViewHeight() {
        val totalHeight = commentDataList.size *81
        val params = binding.lvComment.layoutParams
        params.height = totalHeight.dpToPx()
        binding.lvComment.layoutParams = params
        binding.lvComment.requestLayout()


    }

    fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.btn_fix->{
                val intent = Intent(this, FixBoard::class.java)
                intent.putExtra("key", key)
                intent.putExtra("name", name)
                startActivity(intent)
                finish()
                true
            }
            R.id.btn_delete->{
                Firebase.database.getReference("EveryCommunity").child(key).removeValue()
                Toast.makeText(this,"글이 삭제되었습니다",Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(menuCheck) {
            menuInflater.inflate(R.menu.menu_fix_board, menu)
        }
        return true
    }

}