package com.example.malangtrip.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.malangtrip.MainScreen
import com.example.malangtrip.databinding.ActivityCompleteJoinBinding

class CompleteJoin : AppCompatActivity() {
    private lateinit var binding: ActivityCompleteJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //홈으로 이동
        binding.btnGoHome.setOnClickListener {
            startMainScreen()
        }
        //첫 이메일 로그인 화면으로 이동
        binding.btnGoLogin.setOnClickListener {
            goEmailLogin()
        }

    }
    private fun startMainScreen()
    {
            startActivity(Intent(this,MainScreen::class.java))
            finish()
    }
    private fun goEmailLogin()
    {
        startActivity(Intent(this,EmailLogin::class.java))
        finish()
    }
}