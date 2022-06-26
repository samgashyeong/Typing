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
import com.example.typing.databinding.ActivityRankBinding
import com.google.firebase.firestore.FirebaseFirestore

class RankingAdapter(val data : ArrayList<Ranking>, val binding : ActivityRankBinding,val uid : String) : RecyclerView.Adapter<RankingAdapter.MyViewHolder>() {
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
        if(position < 2){
            holder.background.setBackgroundResource(R.drawable.recyclerview_boder_ranker)
        }
        if(data[position].uid == uid){
            binding.rankingTv.text = "${position+1}등"
            binding.steersmanTv.text = "${data[position].typingSpeed}타"
        }
        val docRef = FirebaseFirestore.getInstance().collection("user").document(data[position].uid)
        docRef.get().addOnSuccessListener { document ->
            if(document != null) {
                val nick = document.getString("nickName")
                holder.userName.text = nick
                return@addOnSuccessListener
            }
        }
        //holder.userName.text = data[position].uid
        holder.steersman.text = "${data[position].typingSpeed}타"
        holder.rank.text = "${position+1}등"
    }

    override fun getItemCount(): Int {
        return data.size
    }
}