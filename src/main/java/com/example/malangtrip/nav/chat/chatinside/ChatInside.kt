package com.example.malangtrip.nav.chat.chatinside

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.malangtrip.key.ChatPageInfo
import com.example.malangtrip.key.UserInfo
import com.example.malangtrip.R
import com.example.malangtrip.key.DBKey
import com.example.malangtrip.key.GetTime
import com.example.malangtrip.databinding.ActivityChatInsideBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class ChatInside : AppCompatActivity() {
    private lateinit var binding : ActivityChatInsideBinding
    private lateinit var chatScreenAdapter: ChatInsideAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var chatRoomId: String = ""  /////////////////
    private var friendId: String = ""    /////////////////
    private var myId: String = ""    /////////////////

    private var friendFcmToken: String = ""

    private var myNickname: String = ""

    //채팅을 하나씩 받을꺼기 떄문에 리스트 하나 만들어주기
    private var chatItemList = mutableListOf<ChatPageInfo>()/////////////////////////////

    //전송 버튼 활성화하는 변수
    private var sendCheck = false

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityChatInsideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val value = intent.getStringExtra("friendName")
        val actionBar = supportActionBar
        actionBar?.setTitle("$value")

        chatRoomId = intent.getStringExtra(EXTRA_CHAT_ROOM_ID)?:return/////////////////
        friendId = intent.getStringExtra(Extra_Frineds_Id)?:return/////////////////
        myId = Firebase.auth.currentUser?.uid?:"" /////////////////



        createAdapter()

        //채팅창 누르면 위로 스크롤롤
       binding.evMessage.setOnClickListener {
            linearLayoutManager.smoothScrollToPosition(binding.rvChatList,null,chatScreenAdapter.itemCount)
            Log.d("샌드버튼 동작함 닉네임 체크11111","$myNickname")
        }
        //키보드 나타났을 때 채팅창 위로 올림
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
                linearLayoutManager.smoothScrollToPosition(binding.rvChatList,null,chatScreenAdapter.itemCount)
        }

        //전송버튼 구현
        binding.btnSend.setOnClickListener {
            sendMessage()
        }


    }
    //상대방 정보 가져오기
    private fun getFriendData()
    {
        Firebase.database.reference.child(DBKey.DB_USERS).child(friendId).get()
            .addOnSuccessListener {
                val friendInfo = it.getValue(UserInfo::class.java)
                friendFcmToken = friendInfo?.fcmToken.orEmpty()
                chatScreenAdapter.friendItem = friendInfo
                sendCheck = true
                getChatData()//채팅 데이터 불러오기
            }//상대방 조회
    }
    //채팅 정보 가져오기기
    private fun getChatData()
    {
        Firebase.database.reference.child(DBKey.DB_CHATS).child(chatRoomId).addChildEventListener(object :ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) { // 어뎁터에 메세지 정보 전송
                val chatItem = snapshot.getValue(ChatPageInfo::class.java)
                chatItem?:return
                chatItemList.add(chatItem)
                chatScreenAdapter.submitList(chatItemList.toMutableList())
            }
            //기능 구현 필요 x
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    //혹시 모를 상황 대비
    companion object{
        const val EXTRA_CHAT_ROOM_ID = "chatRoomId"
        const val Extra_Frineds_Id = "friendId"
    }
    private fun createAdapter()
    {
        chatScreenAdapter = ChatInsideAdapter()
        linearLayoutManager = LinearLayoutManager(applicationContext)
        Firebase.database.reference.child(DBKey.DB_USERS).child(myId).get()
            .addOnSuccessListener {
                val MyInfo = it.getValue(UserInfo::class.java)
                myNickname = MyInfo?.nickname?:"" //수정필요
                getFriendData()//상대방 조회
            }//내 정보 조회



        binding.rvChatList.apply {///////////////////
            layoutManager = linearLayoutManager
            adapter =chatScreenAdapter

        }
        chatScreenAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)


                linearLayoutManager.smoothScrollToPosition(binding.rvChatList,null,chatScreenAdapter.itemCount)
            }
        })
    }
    private fun sendMessage()
    {
        val message = binding.evMessage.text.toString()

        if(sendCheck==false)
        {
            return
        }
        if(message.isEmpty())
        {
            //나중에 전송버튼 비활성화
            return
        }

        val newMessage = ChatPageInfo(
            message=message,
            userId=myId

        )

        Firebase.database.reference.child(DBKey.DB_CHATS).child(chatRoomId).push().apply {
            newMessage.chatId=key
            setValue(newMessage)
        }
        //db업데이트
        val updates: MutableMap<String,Any> = hashMapOf(
            "${DBKey.DB_CHAT_ROOMS}/$myId/$friendId/lastMessage" to message,
            "${DBKey.DB_CHAT_ROOMS}/$friendId/$myId/lastMessage" to message,
            "${DBKey.DB_CHAT_ROOMS}/$friendId/$myId/chatRoomId" to chatRoomId,
            "${DBKey.DB_CHAT_ROOMS}/$friendId/$myId/friendId" to myId,
            "${DBKey.DB_CHAT_ROOMS}/$friendId/$myId/friendName" to myNickname,
            "${DBKey.DB_CHAT_ROOMS}/$myId/$friendId/lastMessageTime" to GetTime.getTime(),
            "${DBKey.DB_CHAT_ROOMS}/$friendId/$myId/lastMessageTime" to GetTime.getTime()
        )
        Log.d("샌드버튼 동작함 닉네임 체크","$myNickname")
        Firebase.database.reference.updateChildren(updates)
        //http송신
        val client = OkHttpClient()
        val root = JSONObject()
        val notification = JSONObject()
        notification.put("title", getString(R.string.app_name))
        notification.put("body", message)

        root.put("to", friendFcmToken)
        root.put("priority", "high")
        root.put("notification", notification)

        val requestBody =
            root.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request =
            Request.Builder().post(requestBody).url("https://fcm.googleapis.com/fcm/send")
                .header("Authorization", "key=AAAAx8LZ-Wo:APA91bHXs1xZmDSCP1x6w1Iv-ujY3Xq3h_50w5l5TlDLtdHWdCzh5BBqSHkOdkKXKHrK2tVyB7XAGVjJ1o50YBPIru1UbOg-HNzxzmJSWxAxU1R6TtHNlqDhyk_Xf8Rhw-JENVMKiuly").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.stackTraceToString()
            }

            override fun onResponse(call: Call, response: Response) {
                // ignore onResponse
            }

        })
        binding.evMessage.text.clear()//전송시 에딧 텍스트 비워줌
    }
}