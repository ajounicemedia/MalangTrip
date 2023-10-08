package com.example.malangtrip.nav.community.boardscreen

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.malangtrip.key.CommunityItem
import com.example.malangtrip.nav.community.readcommunity.GoToBoard
import com.example.malangtrip.nav.community.writecommunity.WriteText
import com.example.malangtrip.R
import com.example.malangtrip.databinding.FragmentEveryBoardWritingBinding
import com.example.malangtrip.key.DBKey
import com.example.malangtrip.key.DBKey.Companion.Community_Key
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//전체게시판
class EveryBoardScreen : Fragment() {
    private var _binding: FragmentEveryBoardWritingBinding? = null
    private val binding get() = _binding!!
    private val everyBoardList = mutableListOf<CommunityItem>()
    private val boardKeyList = mutableListOf<String>()
    private lateinit var everyBoardAdapter : BoardAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentEveryBoardWritingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setHasOptionsMenu(true)
       

        //데이터 받아오기
        getEveryBoardInfo()
        return root


    }
    private fun createAdapter()
    {
        everyBoardAdapter = BoardAdapter(everyBoardList){communityItem->
            val intent = Intent(context, GoToBoard::class.java)
            intent.putExtra("name",communityItem.userName)
            intent.putExtra("key", communityItem.communityKey)
            startActivity(intent)
        }
        binding.rvBoard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = everyBoardAdapter
        }
    }
    private fun getEveryBoardInfo()
    {
        val postListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                everyBoardList.clear()
                if (snapshot.exists()) {
                    for (WriteSnapshot in snapshot.children) {


                        val item = WriteSnapshot.getValue(CommunityItem::class.java)

                        everyBoardList.add(item!!)
                        boardKeyList.add(WriteSnapshot.key.toString())
                    }
                    boardKeyList.reverse()
                    everyBoardList.reverse()
                    //게시판 어댑터 생성
                    createAdapter()
                    everyBoardAdapter.notifyDataSetChanged()
                }
                else
                {
                    // Users 경로에 데이터가 없음
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 쿼리 취소 시
            }
        }
        Firebase.database(DBKey.DB_URL).reference.child(Community_Key).addValueEventListener(postListener)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_community, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    //글쓰기로 이동, 글 재생성
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_write -> {
                // 버튼이 클릭되었을 때의 동작을 여기에 코딩합니다.
                startActivity(Intent(context, WriteText::class.java))
                true
            }
            R.id.btn_reset->
            {
                getEveryBoardInfo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}