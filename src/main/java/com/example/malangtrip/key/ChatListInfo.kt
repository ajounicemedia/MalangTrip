package com.example.malangtrip.key

//선택한 친구 정보
data class ChatListInfo(
    val chatRoomId: String?=null,
    val lastMessage: String?=null,
    val lastMessageTime: String?=null,
    val friendName: String?=null,
    val friendId: String?=null
)