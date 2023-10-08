package com.example.malangtrip.unusedcode

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.malangtrip.MainScreen
import com.example.malangtrip.nav.home.MainHome
import com.example.malangtrip.R
import com.example.malangtrip.databinding.FragmentChatBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainChat : Fragment(){

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // 액션바 설정, 이름변경
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.title = "말랑톡"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        // 뷰페이저 어댑터 생성
        val viewPagerAdapter = ChatViewPagerAdapter(childFragmentManager, lifecycle)

        // 뷰페이저 설정
        val viewPager = binding.viewPager
        viewPager.adapter = viewPagerAdapter

        // 탭 레이아웃과 뷰페이저 연결
        val tabLayout = binding.tabs
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "채팅방 목록"
                1 -> "친구 찾기"
                //2 -> "친구 목록"
                else -> ""
            }
        }.attach()



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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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