package com.example.malangtrip.nav.chat.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.malangtrip.key.ChatListInfo
import com.example.malangtrip.nav.chat.chatinside.ChatInside
import com.example.malangtrip.R
import com.example.malangtrip.key.DBKey.Companion.DB_CHAT_ROOMS
import com.example.malangtrip.MainScreen
import com.example.malangtrip.nav.home.MainHome
import com.example.malangtrip.databinding.FragmentChatListBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//채팅 목록 EX)카톡 두번째 페이지
class ChatList : Fragment() {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.title = "말랑톡"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        //챗어뎁터
        val chatListAdapter = ChatListAdapter{ chatItem->
            //눌렀을 씨 채팅방 이동
            val intent = Intent(context, ChatInside::class.java)
            intent.putExtra(ChatInside.Extra_Frineds_Id,chatItem.friendId)
            intent.putExtra(ChatInside.EXTRA_CHAT_ROOM_ID,chatItem.chatRoomId)
            intent.putExtra("friendName",chatItem.friendName)
            startActivity(intent)
        }
        binding.rvChatList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = chatListAdapter
        }

        val curruntId = Firebase.auth.currentUser?.uid ?: "" // 파이어베이스현재유저체크
        val chatRoomsDb= Firebase.database.reference.child(DB_CHAT_ROOMS).child(curruntId)//채팅방db

        chatRoomsDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val chatRoomList = snapshot.children.mapNotNull {
                    it.getValue(ChatListInfo::class.java)
                }
                chatListAdapter.submitList(chatRoomList)

            }

            override fun onCancelled(error: DatabaseError) {}
        })
        // 뒤로가기 버튼 처리 기본 뒤로가기 버튼 눌렀을 때 홈 프래그먼트로
        root.isFocusableInTouchMode = true
        root.requestFocus()
        root.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                // 현재 프래그먼트가 액티비티에 연결되어 있을 때에만 동작
                if (isAdded) {
                    val mainActivity = activity as? MainScreen
                    mainActivity?.binding?.navigationView?.selectedItemId = R.id.item_home
                }

                val homeFragment = MainHome()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, homeFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                true
            } else {
                false
            }
        }

            return root
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                //requireActivity().onBackPressed()
                val homeFragment = MainHome()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, homeFragment)
                transaction.addToBackStack(null)
                transaction.commit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}