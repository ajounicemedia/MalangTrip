package com.example.malangtrip.unusedcode.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.malangtrip.key.ChatListInfo
import com.example.malangtrip.nav.chat.chatinside.ChatInside
import com.example.malangtrip.R
import com.example.malangtrip.databinding.FragmentUserlistBinding
import com.example.malangtrip.key.DBKey.Companion.DB_CHAT_ROOMS
import com.google.firebase.auth.ktx.auth


import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID

//채팅 목록 EX)카톡 두번째 페이지
class UserList() : Fragment(R.layout.fragment_userlist) {
    private lateinit var binding : FragmentUserlistBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserlistBinding.bind(view)


        //상대방이 날 눌렀을 때
        val Userlistadapter = UserAdapter { friend ->
            val My_Id = Firebase.auth.currentUser?.uid ?: ""
            val chat_room_db = Firebase.database.reference.child(DB_CHAT_ROOMS).child(My_Id)
                .child(friend.userId ?: "")

            chat_room_db.get().addOnSuccessListener {
                var chat_rood_id = ""
                if (it.value != null) {
                    val chat_room = it.getValue(ChatListInfo::class.java)
                    chat_rood_id = chat_room?.chatRoomId ?: ""
                } else {
                    chat_rood_id = UUID.randomUUID().toString()
                    val new_chat_room = ChatListInfo(
                        chatRoomId = chat_rood_id,
                        friendName = friend.nickname,
                        friendId = friend.userId
                    )
                    chat_room_db.setValue(new_chat_room)
                }
                val intent = Intent(context, ChatInside::class.java)
                intent.putExtra(ChatInside.Extra_Frineds_Id, friend.userId)
                intent.putExtra(ChatInside.EXTRA_CHAT_ROOM_ID, chat_rood_id)
                intent.putExtra("friend_Name", friend.nickname)
                startActivity(intent)
            }


        }

        binding.rvUserlist.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = Userlistadapter
        }
        val myId = Firebase.auth.currentUser?.uid ?: ""
    }



}
































//class User_List : Fragment(R.layout.n_chat_userlist) {
//    private lateinit var binding: NChatUserlistBinding
//    private lateinit var userListadapter: User_Adapter
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding = NChatUserlistBinding.bind(view)
//
//        //상대방이 날 눌렀을 때
//        val userListadapter = User_Adapter { friend ->
//            val My_Id = Firebase.auth.currentUser?.uid ?: ""
//            val chat_room_db = Firebase.database.reference.child(DB_CHAT_ROOMS).child(My_Id)
//                .child(friend.userId ?: "")
//
//            chat_room_db.get().addOnSuccessListener {
//                var chat_rood_id = ""
//                if (it.value != null) {
//                    val chat_room = it.getValue(Chat_Info::class.java)
//                    chat_rood_id = chat_room?.chatRoomId ?: ""
//                } else {
//                    chat_rood_id = UUID.randomUUID().toString()
//                    val new_chat_room = Chat_Info(
//                        chatRoomId = chat_rood_id,
//                        friend_Name = friend.nickname,
//                        friend_Id = friend.userId
//                    )
//                    chat_room_db.setValue(new_chat_room)
//                }
//                val intent = Intent(context, Chat_Screen::class.java)
//                intent.putExtra(Chat_Screen.Extra_Frineds_Id, friend.userId)
//                intent.putExtra(Chat_Screen.EXTRA_CHAT_ROOM_ID, chat_rood_id)
//                intent.putExtra("friend_Name", friend.nickname)
//                startActivity(intent)
//            }
//
//            binding.userListRecyclerView.apply {
//                layoutManager = LinearLayoutManager(context)
//                adapter = userListadapter
//            }
//
//            val myId = Firebase.auth.currentUser?.uid ?: ""
//
//            Firebase.database.reference.child(DB_Friends).child(myId)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onCancelled(error: DatabaseError) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        val userlist = mutableListOf<User_Info>()
//
//                        snapshot.children.forEach {
//                            val user = it.getValue(User_Info::class.java)
//                            user ?: return
//                            Log.d("이름 잘 배껴오나", "$user ")
//                            userlist.add(user)
//                        }
//                        userListadapter.submitList(userlist)
//                    }
//                })
//
//            viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
//                @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//                fun refresh() {
//                    Firebase.database.reference.child(DB_Friends).child(myId)
//                        .addListenerForSingleValueEvent(object : ValueEventListener {
//                            override fun onCancelled(error: DatabaseError) {
//                                TODO("Not yet implemented")
//                            }
//
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                val userlist = mutableListOf<User_Info>()
//
//                                snapshot.children.forEach {
//                                    val user = it.getValue(User_Info::class.java)
//                                    user ?: return
//                                    Log.d("이름 잘 배껴오나", "$user ")
//                                    userlist.add(user)
//                                }
//                                userListadapter.submitList(userlist)
//                            }
//                        })
//                }
//            })
//        }
//    }
//}
