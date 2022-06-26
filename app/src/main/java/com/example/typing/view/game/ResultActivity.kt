package com.example.typing.view.game

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.typing.R
import com.example.typing.databinding.ActivityResultBinding
import com.example.typing.view.MainActivity
import com.example.typing.view.util.NetworkStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val averTPM = intent.getDoubleExtra("averTPM", 0.0)
        val clearedTextCount = intent.getIntExtra("stage", 1)

        val status = NetworkStatus.getConnectivityStatus(this)

        binding.tpmTv.text = String.format("%.1f타/분", averTPM)
        binding.stageTv.text = String.format("%d개", clearedTextCount)

        if(clearedTextCount >= 5 && status != NetworkStatus.TYPE_NOT_CONNECTED && auth.currentUser != null) {
            binding.notAplliedToRank.visibility = View.INVISIBLE
            db.collection("rank").document("rank").get()
                .addOnCompleteListener {
                    if(it.isSuccessful && it.result?.data != null){
                        val list = it.result!!.data!!.getValue("rank") as ArrayList<HashMap<String, Any>>
                        var oldData: HashMap<*, *>? = null
                        list.forEach { m ->
                            if(m["uid"] == auth.currentUser!!.uid) oldData = m
                        }
                        if(oldData != null) {
                            if((oldData!!["typingSpeed"] as Double) < averTPM) {
                                list.remove(oldData)
                                list.add(hashMapOf(
                                    "uid" to auth.currentUser!!.uid,
                                    "typingSpeed" to averTPM
                                ))
                            }
                        }
                        else list.add(hashMapOf(
                            "uid" to auth.currentUser!!.uid,
                            "typingSpeed" to averTPM
                        ))
                        db.collection("rank").document("rank").update("rank", list)
                    }
                }
        }
        else {
            binding.notAplliedToRank.visibility = View.VISIBLE
        }

        Log.d(TAG, "onCreate: " + intent.getDoubleExtra("averTPM", 0.0))

        binding.button.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}