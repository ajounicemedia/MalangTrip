package com.example.malangtrip.unusedcode

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.malangtrip.nav.chat.chatlist.ChatList

//채팅 탭 레이아웃 관리하는 어뎁터
class ChatViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount() = 2 // 탭 수에 맞게 설정

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            //0 -> User_List()
            0 -> ChatList()
            1 -> SearchFriend()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}