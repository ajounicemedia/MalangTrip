package com.example.malangtrip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.malangtrip.key.DBKey
import com.example.malangtrip.key.UserInfo
import com.example.malangtrip.databinding.ActivitySplashBinding
import com.example.malangtrip.login.EmailLogin
import com.example.malangtrip.login.UserDataInput
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SplashActivity : AppCompatActivity() {
    val usersRef = Firebase.database.reference.child(DBKey.DB_USERS)
    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide() // 액션바 숨김
        var loginCheck = false
        val curruntId= Firebase.auth.currentUser?.uid?:""

        Handler(Looper.getMainLooper()).postDelayed({
            if(curruntId==null||curruntId=="")
            {
                Log.d("123","지금 여기 거치는 중")
                startActivity(Intent(this, EmailLogin::class.java))
                finish()
            }
            if (curruntId.isNotEmpty()) {
                val mydb = usersRef.child(curruntId)

                mydb.get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                    } else {
                        startActivity(Intent(this, EmailLogin::class.java))
                    }
                    finish()
                }
            }
            val mydb = usersRef.child(curruntId)//내 정보 접근
            mydb.get().addOnSuccessListener {

                val myinfo = it.getValue(UserInfo::class.java)?: return@addOnSuccessListener

                val nickName = myinfo.nickname
                if(nickName==null)
                {
                    startActivity(Intent(this, UserDataInput::class.java))
                    loginCheck = true
                    finish()
                    return@addOnSuccessListener
                }
                else{
                    startActivity(Intent(this, MainScreen::class.java))
                    loginCheck = true
                    finish()
                    return@addOnSuccessListener
                }
                if(loginCheck==false) {
                    startActivity(Intent(this, EmailLogin::class.java))
                    finish()
                }
            }

        }, 1500) // 3000ms = 3초 동안 스플래시 스크린을 보여줍니다.

    }

}