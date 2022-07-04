package kr.tangram.smartgym.ui.main.map.challenge

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kr.tangram.smartgym.databinding.ItemChallengeListBinding


class GymChallengeRecyclerViewAdapter: RecyclerView.Adapter<GymChallengeRecyclerViewAdapter.ViewHolder>() {

    private var challengeList : MutableList<String> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChallengeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun setList(list: MutableList<String>){
        challengeList.clear()
        this.challengeList.addAll(list)
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!challengeList.isNullOrEmpty()){
            val item = challengeList[position]
        }
    }

    override fun getItemCount(): Int = challengeList.size


    inner class ViewHolder(binding: ItemChallengeListBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}