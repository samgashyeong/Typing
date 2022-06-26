package com.example.typing.view.login

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.typing.R
import com.example.typing.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.function.Consumer
import kotlin.properties.Delegates

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        binding.loginBt.setOnClickListener {
            distinctNickName(binding.nickNameEt.text.toString()) {
                it
                Log.d(TAG, "onCreate: ")
                if(binding.emailEt.text.isBlank()
                    or binding.passwordEt.text.isBlank()
                    or binding.passwordAgainEt.text.isBlank()
                    or binding.nickNameEt.text.isBlank()){
                    Toast.makeText(this, "빈칸을 입력해주세요!", Toast.LENGTH_SHORT).show()
                }
                else if(binding.passwordEt.text.toString() != binding.passwordAgainEt.text.toString()){
                    Toast.makeText(this, "비밀번호가 맞지않습니다!", Toast.LENGTH_SHORT).show()
                }
                else if(binding.passwordEt.text.length <6){
                    Toast.makeText(this, "비밀번호는 6글자 이상이여야합니다.", Toast.LENGTH_SHORT).show()
                }
                else if(it){
                    Log.d(TAG, "onCreate: 중복 닉네임 체크")
                    Toast.makeText(this, "이미 사용중인 닉네임입니다.", Toast.LENGTH_SHORT).show()
                }
                else{
                    auth.createUserWithEmailAndPassword(
                        binding.emailEt.text.toString().trim(),
                        binding.passwordEt.text.toString()
                    ).addOnSuccessListener {
                        login(binding.emailEt.text.toString().trim()
                            ,binding.passwordEt.text.toString()
                            ,binding.nickNameEt.text.toString())
                    }
                }
            }
        }

    }

    @SuppressLint("NewApi")
    private fun distinctNickName(nickName: String, consumer: Consumer<Boolean>) {
        db.collection("user").get()
            .addOnSuccessListener {
                var distinct = false
                it.documents.forEach { doc ->
                    Log.d(TAG, "distinctNickName: ${doc.data}")
                    if (nickName == doc.data?.get("nickName").toString()) {
                        distinct = true
                    }
                }
                consumer.accept(distinct)
            }

    }

    private fun login(email: String, password: String, nickName: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            applyDb(email, nickName)
        }.addOnFailureListener {
            Toast.makeText(this, "죄송합니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyDb(email: String, nickName: String) {
        val data = hashMapOf(
                "email" to email,
                "nickName" to nickName,
                "uid" to auth.currentUser?.uid
        )
        db.collection("user").document(auth.currentUser?.uid.toString())
            .set(data)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}