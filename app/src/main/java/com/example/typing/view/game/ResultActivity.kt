package com.example.typing.view.game

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.typing.R
import com.example.typing.databinding.ActivityResultBinding
import com.example.typing.view.MainActivity


class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result)

        binding.tpmTv.text = String.format("%.1f타/분", intent.getDoubleExtra("averTPM", 0.0))
        binding.stageTv.text = String.format("%d개", intent.getIntExtra("stage", 1))

        Log.d(TAG, "onCreate: " + intent.getDoubleExtra("averTPM", 0.0))

        binding.button.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}