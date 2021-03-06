package com.example.typing.view

import android.content.ContentValues.TAG
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
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
import java.util.function.BooleanSupplier


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
            if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
                Toast.makeText(this, "인터넷을 연결한 후에 시도해주세요", Toast.LENGTH_SHORT).show()
            }
            else if(!this::userName.isInitialized) {
                Toast.makeText(this, "유저 정보가 로딩되지 않았습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                startActivity(
                    Intent(this, RankActivity::class.java)
                        .putExtra("nickName", userName)
                        .putExtra("uid", userUid)
                )
            }
        }

        binding.userNameTv.setOnClickListener {
            showAlertDialog(if(auth.currentUser == null) "로그인" else "로그아웃", "로그인 화면으로 가시겠습니까?", {
                    _, _ ->
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, {
                    _, _ ->
            })
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

    private fun showAlertDialog(title: String, msg: String,
                                positiveListener: DialogInterface.OnClickListener,
                                negativeListener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton("확인", positiveListener)
        builder.setNegativeButton("취소", negativeListener)
        builder.show()
    }
}