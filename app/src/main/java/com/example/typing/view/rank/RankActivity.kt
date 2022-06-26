package com.example.typing.view.rank

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.typing.R
import com.example.typing.data.Ranking
import com.example.typing.databinding.ActivityRankBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RankActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRankBinding
    private lateinit var rankingArray : ArrayList<Ranking>
    private lateinit var fs : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var nickName : String
    private lateinit var uid : String
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rank)
        setContentView(binding.root)
        Log.w(TAG, "onCreate:데이터 받아오기 ㄹㄷㅈㄹㅈㄷㄹㅈㄷㄹ")
        nickName = intent.getStringExtra("nickName").toString()
        uid = intent.getStringExtra("uid").toString()
        Log.d(TAG, "onCreate: 유저 정보 ${nickName} ${uid}")
        fs = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        rankingArray = ArrayList()

        Log.d(TAG, "onCreate: ${auth.currentUser?.email} ${auth.currentUser?.displayName}")
        binding.recycler.adapter = RankingAdapter(rankingArray, binding, uid)
        setUi()
    }

    private fun setUi() {
        binding.userNameTv.text = nickName
        fs.collection("rank").document("rank").get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d(TAG, "onCreate:데이터 받아오기 ${it.result}")
                    val list = it.result!!.data!!.getValue("rank") as ArrayList<HashMap<*, *>>
                    val uid = ArrayList<String>()
                    for(i in list){
                        rankingArray.add(Ranking(i["uid"].toString(), i["typingSpeed"].toString().toInt()))
                    }
                    rankingArray.sortByDescending {
                        it.typingSpeed
                    }


                    Log.d(TAG, "onCreate: $rankingArray")
                    renamefirebase(rankingArray)
                }
            }
    }


    private fun renamefirebase(rankingArray: ArrayList<Ranking>) {
        Log.d(TAG, "renamefirebase: 적용하기")
        Log.d(TAG, "renamefirebase: ${rankingArray}")
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = RankingAdapter(rankingArray, binding, uid)
    }

//    private fun getName(uid: String): String {
//        val docRef = fs.collection("user").document(uid)
//        docRef.get().addOnSuccessListener { document ->
//            if(document != null) {
//                val nick = document.getString("nickName")
//                item.nickname = nick
//                return@addOnSuccessListener
//            }
//        }
//    }
}