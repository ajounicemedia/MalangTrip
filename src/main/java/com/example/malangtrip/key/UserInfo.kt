package com.example.malangtrip.key

//유저 정보
data class UserInfo(
    val userId: String?=null,
    val name: String?=null,
    val email:String?=null,
    val phoneNumner:Int?=null,
    val gender:Boolean?=null,
    val nickname: String?=null,
    val description: String?=null,
    val fcmToken: String?=null,
    val driverCheck: Boolean=false,
    val bank : String?=null,
    val bankNum:String?=null

)