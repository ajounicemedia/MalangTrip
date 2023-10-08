package com.example.malangtrip.nav.home

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.malangtrip.key.ChatListInfo
import com.example.malangtrip.nav.chat.chatinside.ChatInside
import com.example.malangtrip.key.GetTime
import com.example.malangtrip.key.TripInfo
import com.example.malangtrip.R
import com.example.malangtrip.databinding.ActivityTripTextBinding
import com.example.malangtrip.key.DBKey
import com.example.malangtrip.key.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*


class TripText : AppCompatActivity(){
    lateinit var binding : ActivityTripTextBinding
    lateinit var driverId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tripId = intent.getStringExtra("trip_Id").toString()
        driverId = intent.getStringExtra("driver_Id").toString()



        getTripData(tripId)
        binding.btnCheckProfile.setOnClickListener {
            checkProfile()
        }
        binding.btnChat.setOnClickListener {
            chat()
        }


    }
    private fun checkProfile()
    {
        val intent = Intent(this,DriverProfile::class.java)
        val imageUrl = driverId+".png"
        intent.putExtra("DriverKey",driverId)
        intent.putExtra("image_url", imageUrl)
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child(imageUrl)
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload()
        }




        startActivity(intent)
    }
    private fun chat()
    {
        val myId = Firebase.auth.currentUser?.uid ?: ""
        val chatRoomDb = Firebase.database.reference.child(DBKey.DB_CHAT_ROOMS).child(myId)
            .child(driverId)
        val driverKey = Firebase.database.reference.child(DBKey.DB_USERS).child(driverId)
        driverKey.get().addOnSuccessListener {
            val driver = it.getValue(UserInfo::class.java)

            chatRoomDb.get().addOnSuccessListener {
                var chatRoodId = ""
                if (it.value != null) {
                    val chatRoom = it.getValue(ChatListInfo::class.java)
                    chatRoodId = chatRoom?.chatRoomId ?: ""
                } else {
                    chatRoodId = UUID.randomUUID().toString()
                    val newChatRoom = ChatListInfo(
                        chatRoomId = chatRoodId,
                        friendName = driver?.nickname,
                        friendId = driver?.userId


                    )
                    chatRoomDb.setValue(newChatRoom)
                }
                val intent = Intent(this, ChatInside::class.java)
                intent.putExtra(ChatInside.Extra_Frineds_Id, driver?.userId)
                intent.putExtra(ChatInside.EXTRA_CHAT_ROOM_ID, chatRoodId)
                intent.putExtra("friendName", driver?.nickname)
                startActivity(intent)
            }
        }
    }

    private fun getTripData(key:String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {

                    val dataModel = dataSnapshot.getValue(TripInfo::class.java)


                    //binding.boardTitle.text = dataModel!!.title
                    supportActionBar?.apply {
                        title = "여행 제목 : " + dataModel!!.title
                        setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 표시
                        supportActionBar?.setHomeAsUpIndicator(R.drawable.my_home_back)
                    }

                    binding.tvTripDate.text = "여행 일정 : " + dataModel!!.schedule
                    binding.tvWriteTime.text = "작성 시간 : " + GetTime.getTime()
                    binding.tvTripPrice.text="가격 : " + dataModel!!.price+"원"
                    binding.tvTripContent.text = "여행 내용 : " + dataModel!!.content

                } catch (e: Exception) {

                    Log.d(ContentValues.TAG, "삭제완료")

                }





            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        Firebase.database(DBKey.DB_URL).reference.child(DBKey.Trip_Info).child(driverId).child(key).addValueEventListener(postListener)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }
}