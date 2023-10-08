package com.example.malangtrip.unusedcode

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment


import com.example.malangtrip.key.DBKey
import com.example.malangtrip.databinding.FragmentSearchFriendBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SearchFriend : Fragment(){

    private var _binding: FragmentSearchFriendBinding? = null
    private val binding get() = _binding!!
    private var myNickname =""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _binding = FragmentSearchFriendBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //내 이름 조회
        val currentUserId = Firebase.auth.currentUser?.uid
        if (currentUserId != null) {
            getCurrentUserNickname(currentUserId) { nickname ->
                myNickname = nickname
                Log.d("내 이름 잘 배껴오나", "$myNickname")
            }
        }



        binding.btnFriendAdd.setOnClickListener {
            val friendName = binding.etFriendName.text.toString()

            if(myNickname==friendName)
            {
                Toast.makeText(context,"자신의 닉네임은 검색할 수 없습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //이름 검색
            findFriend(friendName)

        }

        return root
    }
    private fun findFriend(friendName: String) {

        // 파이어베이스에서 유저 데이터 가져오기
        val curruntUser = Firebase.auth.currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var isFound = false // 닉네임을 찾았는지 여부를 체크하는 변수
                    for (userSnapshot in snapshot.children) {
                        val nickname = userSnapshot.child("nickname").value as String
                        if (nickname == friendName) {
                            isFound = true // 닉네임을 찾았음을 표시

                            val myId = curruntUser?.uid

                            val userId = userSnapshot.child("userId").value as String
                            val description = userSnapshot.child("description").value as String
                            val fcmToken = userSnapshot.child("fcmToken").value as String
                            val infoFriend = mutableMapOf<String,Any>()
                            infoFriend["nickname"]=nickname
                            infoFriend["userId"]=userId
                            infoFriend["description"]=description
                            infoFriend["fcmToken"]=fcmToken
                            Log.d("닉네임확인",nickname)

                            break
                        }

                    }
                    if (!isFound) { // 닉네임을 찾지 못했으면
                        Toast.makeText(context,"없는 닉네임", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    // Users 경로에 데이터가 없음
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 쿼리 취소 시
            }
        })


    }
    private fun getCurrentUserNickname(userId: String, onComplete: (String) -> Unit) {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val nickname = snapshot.child("nickname").value as String
                    onComplete(nickname)

                } else {
                    // 유저 데이터가 없음
                    onComplete("")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 쿼리 취소 시
            }
        })
    }



}