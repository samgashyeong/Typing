package com.example.typing.view.rank

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.typing.R
import com.example.typing.data.Ranking

class RankingAdapter(val data : ArrayList<Ranking>) : RecyclerView.Adapter<RankingAdapter.MyViewHolder>() {
    init {
        Log.d(TAG, "외 안될까: ")
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var userName: TextView = itemView.findViewById(R.id.userNameTv)
        val steersman : TextView= itemView.findViewById(R.id.steersmanTv)
        val rank : TextView = itemView.findViewById(R.id.rankingTv)
        val background : ConstraintLayout = itemView.findViewById(R.id.constraintLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ranking_layout, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: ${data[position]}")
        if(position < 1){
            holder.background.setBackgroundResource(R.drawable.recyclerview_boder_ranker)
        }
        holder.userName.text = data[position].userName
        holder.steersman.text = "${data[position].steersman}타"
        holder.rank.text = "${position+1}등"
    }

    override fun getItemCount(): Int {
        return data.size
    }
}