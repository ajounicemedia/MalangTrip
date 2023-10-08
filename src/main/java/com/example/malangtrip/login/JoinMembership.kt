package com.example.malangtrip.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.malangtrip.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


//회원가입
class JoinMembership :AppCompatActivity() {
    lateinit var binding: ActivityJoinBinding

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide() // 액션바 숨김
        mAuth = FirebaseAuth.getInstance()

        binding.btnJoin.setOnClickListener {
            joinMembership()
        }

    }
    private fun joinMembership()
    {
        val email = binding.etIdInput.text.toString()
        val password = binding.etPasswordInput.text.toString()
        val passwordCheck = binding.etPasswordCheck.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일 또는 패스워드가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful&&password==passwordCheck) {
                    // 회원가입 성공
                    Toast.makeText(this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,UserDataInput::class.java))
                    finish()
                } else {
                    // 회원가입 실패
                    if(password!=passwordCheck)
                    {
                        Toast.makeText(this, "비밀번호를 똑같이 입력하지 않으셨습니다", Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(this, "이미 등록된 이메일을 등록하셨거나 이메일 형식으로 입력하지 않으셨습니다", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
