package com.example.malangtrip.nav.myinfo.myprofile

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.malangtrip.databinding.FragmentFixMyprofileBinding
import com.example.malangtrip.key.UserInfo
import com.example.malangtrip.key.DBKey
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//프로필 변경
class FixMyProfile : Fragment() {
    private var _binding: FragmentFixMyprofileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentFixMyprofileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //액션바 활성화 및 이름 변경후 뒤로가기 버튼 활성화
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "나의 프로필"

        //현재 내 정보 가져오기
        val curruntId=Firebase.auth.currentUser?.uid?:"" // 현재 유저 아이디 가져오기
        val myDb = Firebase.database.reference.child(DBKey.DB_USERS).child(curruntId)//내 정보 접근
        val currentUser = Firebase.auth.currentUser
        val email = currentUser?.email.toString()
        myDb.get().addOnSuccessListener {
                val myinfo = it.getValue(UserInfo::class.java)?: return@addOnSuccessListener
            binding.tvNickname.setText(myinfo.nickname)
            binding.tvDes.setText(myinfo.description)
            //binding.InputBank.setText(myinfo.bank)
            binding.tvMyid.setText(" "+email)
            //myinfo.bankNum?.let { it1 -> binding.InputBankNum.setText(it1) }
        }
        //수정한거 적용하는 버튼튼
       binding.btnApply.setOnClickListener {
           apply()
       }

        //메뉴 사용 활성화
        setHasOptionsMenu(true)
        // 뒤로가기 버튼 처리 이전 프래그먼트로 감
        root.isFocusableInTouchMode = true
        root.requestFocus()
        root.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                requireActivity().supportFragmentManager.popBackStack()
                true
            } else {
                false
            }
        }
    binding.btnCancle.setOnClickListener {
        requireActivity().supportFragmentManager.popBackStack()
    }
        return root
    }
    private fun apply()
    {
        val nickname = binding.etFixNickname.text.toString()
        val description = binding.etFixDes.text.toString()

        if (nickname.isEmpty()) {
            Toast.makeText(context, "유저이름은 빈 값으로 둘 수 없습니다", Toast.LENGTH_SHORT).show()
            return
        }
        val curruntId = Firebase.auth.currentUser?.uid ?: "" // 현재 유저 아이디 가져오기
        val myDb = Firebase.database.reference.child(DBKey.DB_USERS).child(curruntId)//내 정보 접근
        //제이슨업데이트
        val myProfile = mutableMapOf<String, Any>()
        myProfile["nickname"] = nickname
        myProfile["description"] = description
        myDb.updateChildren(myProfile)

        requireActivity().supportFragmentManager.popBackStack()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                //requireActivity().onBackPressed()
                requireActivity().supportFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}