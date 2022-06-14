package com.example.typing.view.login

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.typing.R
import com.example.typing.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        binding.loginBt.setOnClickListener {
            if(binding.emailEt.text.isBlank()
                or binding.passwordEt.text.isBlank()
                or binding.passwordAgainEt.text.isBlank()){
                Toast.makeText(this, "빈칸을 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
            else if(binding.passwordEt.text.toString() != binding.passwordAgainEt.text.toString()){
                Toast.makeText(this, "비밀번호가 맞지않습니다!", Toast.LENGTH_SHORT).show()
            }
            else if(binding.passwordEt.text.length <6){
                Toast.makeText(this, "비밀번호는 6글자 이상이여야합니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                auth.createUserWithEmailAndPassword(
                    binding.emailEt.text.toString().trim(),
                    binding.passwordEt.text.toString()
                ).addOnSuccessListener {
                    Toast.makeText(applicationContext, "성공하였습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    }
}