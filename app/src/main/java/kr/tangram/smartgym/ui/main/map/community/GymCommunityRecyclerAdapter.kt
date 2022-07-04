package kr.tangram.smartgym.ui.main.map.community

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kr.tangram.smartgym.databinding.ItemCommunityListBinding

class GymCommunityRecyclerAdapter: RecyclerView.Adapter<GymCommunityRecyclerAdapter.ViewHolder>() {
    private var postList: MutableList<String> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommunityListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    fun setList(list: MutableList<String>){
        postList.clear()
        this.postList.addAll(list)
        notifyDataSetChanged()
        Log.d("리스트", postList.size.toString())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!postList.isNullOrEmpty()){
            val item = postList[position]
        }
    }

    override fun getItemCount(): Int= postList.size


    inner class ViewHolder(binding: ItemCommunityListBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}