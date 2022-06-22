package com.example.typing.view.rank

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.typing.R
import com.example.typing.data.Ranking
import com.example.typing.databinding.ActivityRankBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RankActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRankBinding
    private lateinit var rankingArray : ArrayList<Ranking>
    private lateinit var fs : FirebaseFirestore
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rank)
        setContentView(R.layout.activity_rank)

        fs = FirebaseFirestore.getInstance()
        rankingArray = ArrayList()
        val a = fs.collection("rank").document("rank").get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d(TAG, "onCreate:데이터 받아오기 ${it.result}")
                    val a = it.result!!.data!!.getValue("rank") as ArrayList<HashMap<*, *>>

                    for(i in a){
                        rankingArray.add(Ranking(i["user"].toString(), i["steerman"].toString().toInt()))
                        binding.recycler.adapter = RankingAdapter(rankingArray)
                        binding.rankingTv.text = "`11"
                    }
                }
            }
//        rankingArray = ArrayList()
//
//        rankingArray.add(Ranking("이준상", 100))
//        rankingArray.add(Ranking("이준싱", 14394))
    }
}