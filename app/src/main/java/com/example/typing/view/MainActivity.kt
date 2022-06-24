package com.example.typing.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.typing.R
import com.example.typing.databinding.ActivityMainBinding
import com.example.typing.view.game.GameActivity
import com.example.typing.view.login.LoginActivity
import com.example.typing.view.rank.RankActivity
import com.example.typing.view.util.ResourceLoader
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ResourceLoader.loadResources(assets)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.rankBtn.setOnClickListener {
            startActivity(
                Intent(this, RankActivity::class.java)
            )
        }


        binding.timeAttackBtn.setOnClickListener {
//            startActivity(
//                Intent(this, 클래스이름)
//            )
        }

        binding.normalGameBtn.setOnClickListener {
            startActivity(
                Intent(this, GameActivity::class.java)
            )
            finish()
        }
    }
}