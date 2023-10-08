package com.example.malangtrip.key

import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class GetTime {
    companion object{
        private lateinit var auth: FirebaseAuth

        fun getTime() : String{
            val currentDate = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA)
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")  // Timezone 설정
            return dateFormat.format(currentDate)
        }
    }
}