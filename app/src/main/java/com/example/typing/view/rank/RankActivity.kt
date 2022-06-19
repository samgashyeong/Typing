package com.example.typing.view.rank

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.typing.R
import com.example.typing.data.Ranking
import com.example.typing.databinding.ActivityRankBinding

class RankActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRankBinding
    private lateinit var rankingArray : ArrayList<Ranking>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)

        rankingArray = ArrayList()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rank)

        rankingArray.add(Ranking("이준상", 100))
        rankingArray.add(Ranking("이준싱", 14394))
        binding.recycler.adapter = RankingAdapter(rankingArray)
    }
}