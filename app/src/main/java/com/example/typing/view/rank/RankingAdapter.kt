package com.example.typing.view.rank

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.typing.R
import com.example.typing.data.Ranking

class RankingAdapter(val data : ArrayList<Ranking>) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var userName: TextView = itemView.findViewById(R.id.userNameTv)
        val steersman : TextView= itemView.findViewById(R.id.steersmanTv)
        val rank : TextView = itemView.findViewById(R.id.rankingTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ranking_layout, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: ${data[position]}")
        holder.userName.text = data[position].userName
        holder.steersman.text = "${data[position].steersman}타"
        holder.rank.text = "${position+1}등"
    }

    override fun getItemCount(): Int {
        return data.size
    }
}