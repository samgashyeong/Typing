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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RankActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRankBinding
    private lateinit var rankingArray : ArrayList<Ranking>
    private lateinit var fs : FirebaseFirestore
    init{
        Log.d(TAG, "적용됨: ")
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rank)
        setContentView(binding.root)
        Log.w(TAG, "onCreate:데이터 받아오기 ㄹㄷㅈㄹㅈㄷㄹㅈㄷㄹ")
        fs = FirebaseFirestore.getInstance()
        rankingArray = ArrayList()
        binding.recycler.adapter = RankingAdapter(rankingArray)
        fs.collection("rank").document("rank").get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d(TAG, "onCreate:데이터 받아오기 ${it.result}")
                    val a = it.result!!.data!!.getValue("rank")
                    val list = a as ArrayList<HashMap<*, *>>
                    var steersman = ArrayList<Int>()
                    for(i in list){
                        steersman.add(i["steerman"].toString().toInt())
                    }
                    for(i in 0 until list.size){
                        rankingArray.add(Ranking(list[i]["user"].toString(), steersman[i]))
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
        binding.recycler.adapter = RankingAdapter(rankingArray)
    }
}