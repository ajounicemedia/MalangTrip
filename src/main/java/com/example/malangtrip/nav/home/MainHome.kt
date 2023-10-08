package com.example.malangtrip.nav.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.malangtrip.nav.home.local.MainJeju
import com.example.malangtrip.R
import com.example.malangtrip.databinding.FragmentHomeBinding
import com.example.malangtrip.key.DBKey
import com.example.malangtrip.key.DBKey.Companion.DB_CHAT_ROOMS
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//메인 홈
class MainHome : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //홈에 표시할 지역리스트
    private lateinit var localAdapter: LocalAdapter
    val imageList = listOf(R.drawable.jeju,R.drawable.comming_soon,R.drawable.comming_soon,R.drawable.comming_soon,R.drawable.comming_soon
        ,R.drawable.comming_soon,R.drawable.comming_soon,R.drawable.comming_soon)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //데이타 호출
        getDataFromFirebase()


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // 액션바 설정, 이름변경, 액티비티에 연결되어 있는 프래그먼트이므로 상단 뒤로가기 버튼 없음
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "가고 싶은 여행지를 찾아요"
        actionBar?.setHomeAsUpIndicator(R.drawable.my_home_back) // 홈 버튼 아이콘 변경
        setHasOptionsMenu(true)
        createAdapter()





        // 뒤로가기 버튼을 눌렀을 때 앱 종료
        root.isFocusableInTouchMode = true
        root.requestFocus()
        root.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                showExitDialog()
                return@setOnKeyListener true
            }
            false
        }

        return root
    }
    //홈에 지역 리스트 띄우는 함수
    private fun createAdapter()
    {
        localAdapter = LocalAdapter(imageList)
        binding.rvLocal.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvLocal.adapter = localAdapter
        localAdapter.setOnItemClickListener(object : LocalAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // Handle the item click event here
                if (position == 0) {
                    val jejuFragment = MainJeju()
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, jejuFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                else{
                    Toast.makeText(context, "곧 다른 지역이 추가될 예정입니다!\n기대해주세요!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //뒤로가기 했을 때 종료
    private fun showExitDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("앱 종료")
            .setMessage("정말로 앱을 종료하시겠습니까?")
            .setPositiveButton("종료") { _, _ ->
                activity?.let {
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.startActivity(intent)
                    android.os.Process.killProcess(android.os.Process.myPid())
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }
    //왼쪽 위 뒤로가기 했을 때 종료
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                showExitDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }


    //파이어베이스에서 데이타 호출 한 번에
   private fun getDataFromFirebase(){
        getDataFromFirebaseKey(DBKey.Trip_Info)
        getDataFromFirebaseKey(DBKey.DB_USERS)
        getDataFromFirebaseKey(DBKey.My_Wishlist)
        getDataFromFirebaseKey(DBKey.Community_Key)
        getDataFromFirebaseKey(DBKey.Comment_Key)
        getDataFromFirebaseKey(DB_CHAT_ROOMS)
   }
    //중복되는 코드 없애기 위한 함수
    private fun getDataFromFirebaseKey(dataKey:String)
    {
        if(dataKey==DBKey.DB_USERS||dataKey==DBKey.My_Wishlist||dataKey==DB_CHAT_ROOMS)
        {
            val myid = Firebase.auth.currentUser?.uid ?: ""
            Firebase.database.getReference(dataKey).child(myid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {}
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("데이터 못 불러왔음", dataKey)
                    }
                })
        }
        else{
            Firebase.database.getReference(dataKey)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {}
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("데이터 못 불러왔음", dataKey)
                    }
                })
        }
    }

}