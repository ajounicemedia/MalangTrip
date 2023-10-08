package com.example.malangtrip.key

data class CommunityItem(
    val communityKey:  String="",
    val userId: String="", // 작성자 ID
    val userName: String="", // 작성자 이름
    var title: String="", // 글 제목
    var content: String="", // 글 내용
    var time: String="", // 작성 시간
    var boardType: String = "",//글종류
    var commentNum:Int=0
)