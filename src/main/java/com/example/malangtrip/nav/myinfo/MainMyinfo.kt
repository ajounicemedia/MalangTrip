package com.example.malangtrip.nav.myinfo

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.malangtrip.MainScreen
import com.example.malangtrip.key.UserInfo
import com.example.malangtrip.nav.home.MainHome
import com.example.malangtrip.nav.myinfo.myprofile.FixMyProfile
import com.example.malangtrip.nav.myinfo.driver.ResisterDriver
import com.example.malangtrip.nav.myinfo.driver.ResisterDriverSchedule
import com.example.malangtrip.R
import com.example.malangtrip.databinding.FragmentMainMyinfoBinding
import com.example.malangtrip.key.DBKey
import com.example.malangtrip.nav.community.boardscreen.MyTextList
import com.example.malangtrip.login.EmailLogin
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//내 정보 메인
class MainMyinfo : Fragment() {
    private var _binding: FragmentMainMyinfoBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {


        _binding = FragmentMainMyinfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // 액션바 설정, 이름변경
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.title = "내 정보"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        //내 정보-> 프로필 확인
        binding.btnGoToMyprofile.setOnClickListener {
            goToFragment(FixMyProfile())
        }
        //내 정보 -> 예약일정
//        binding.Reservation.setOnClickListener {
//            val Reservation_Fragment = Main_Reservation()
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragmentContainer, Reservation_Fragment)
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }
        // 드라이버 등록하기로 이동
        binding.btnResisterDriver.setOnClickListener {
            goToFragment(ResisterDriver())
        }
        //나의 작성글로 이동
        binding.btnMyText.setOnClickListener {
            goToFragment(MyTextList())
        }
        // 내 여행 정보 등록
        binding.btnMyScheduleControl.setOnClickListener {
            driverCheck()
        }
        binding.btnGoLogout.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(context, EmailLogin::class.java))
            activity?.finish()
        }

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

                goToFragment(MainHome())
                true
            } else {
                false
            }
        }
        return root
    }

    private fun driverCheck()
    {
        val curruntId = Firebase.auth.currentUser?.uid ?: "" // 현재 유저 아이디 가져오기
        val mydb = Firebase.database.reference.child(DBKey.DB_USERS).child(curruntId)//내 정보 접근
        mydb.get().addOnSuccessListener {
            val myinfo = it.getValue(UserInfo::class.java)?: return@addOnSuccessListener
            val driver_Check = myinfo.driverCheck.toString()
            if(driver_Check=="false")
            {
                Toast.makeText(context,"드라이버로 등록된 사람만 사용할 수 있는 기능입니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                goToFragment(ResisterDriverSchedule())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                //requireActivity().onBackPressed()
                goToFragment(MainHome())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun goToFragment(fragment: Fragment)
    {
        val fragmentPlace = fragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragmentPlace)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}