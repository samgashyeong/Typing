package com.example.typing.view

import android.content.ContentValues.TAG
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.typing.R
import com.example.typing.databinding.ActivityMainBinding
import com.example.typing.view.game.GameActivity
import com.example.typing.view.login.LoginActivity
import com.example.typing.view.login.SignUpActivity
import com.example.typing.view.rank.RankActivity
import com.example.typing.view.util.NetworkStatus
import com.example.typing.view.util.ResourceLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var userName : String
    private lateinit var userEmail : String
    private lateinit var userUid : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ResourceLoader.loadResources(assets)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        if(auth.currentUser == null){
            binding.textView2.visibility = View.VISIBLE
            binding.rankBtn.visibility = View.INVISIBLE
            binding.userNameTv.text = "<offline>"
            Log.d(TAG, "onCreate: 오프라인모드 ")
        }
        else {
            binding.textView2.visibility = View.INVISIBLE
            binding.rankBtn.visibility = View.VISIBLE
            getUserInfo()
        }
        binding.rankBtn.setOnClickListener {
            val status = NetworkStatus.getConnectivityStatus(this)
            if(status == 3){
                Toast.makeText(this, "인터넷을 연결한 후에 시도해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                startActivity(
                    Intent(this, RankActivity::class.java)
                        .putExtra("nickName", userName)
                        .putExtra("email", userEmail)
                )
            }
        }

        binding.timeAttackBtn.setOnClickListener {
            val i = Intent(this, GameActivity::class.java)
            i.putExtra("isTimeAttackMode", true)
            startActivity(i)
            finish()
        }

        binding.normalGameBtn.setOnClickListener {
            startActivity(
                Intent(this, GameActivity::class.java)
            )
            finish()
        }
    }

    private fun getUserInfo() {
        db.collection("user").document(auth.currentUser?.uid.toString()).get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val userInfo = it.result.data
                    userName = userInfo!!.getValue("nickName").toString()
                    userUid = auth.currentUser!!.uid
                    userEmail = auth.currentUser!!.email.toString()
                    Log.d(ContentValues.TAG, "getUse rInfo: ${userInfo}")
                    binding.userNameTv.text = userName
                }
            }
    }
}