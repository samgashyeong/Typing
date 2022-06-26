package com.example.typing.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.typing.view.MainActivity
import com.example.typing.R
import com.example.typing.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.signUpBt.setOnClickListener {
            startActivity(
                Intent(this, SignUpActivity::class.java)
            )
        }

        binding.offineBt.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }

        binding.loginBt.setOnClickListener {
            if (binding.emailEt.text.isBlank() or binding.passwordEt.text.isBlank()){
                Toast.makeText(this, "빈칸을 채워주세요!", Toast.LENGTH_SHORT).show()
            }
            auth.signInWithEmailAndPassword(
                binding.emailEt.text.toString(),
                binding.passwordEt.text.toString()
            ).addOnSuccessListener {
                Toast.makeText(applicationContext, "로그인에 성공했어요!", Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }.addOnFailureListener {
                Toast.makeText(this, "이메일 주소 또는 비밀번호가 맞지 않아요!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}