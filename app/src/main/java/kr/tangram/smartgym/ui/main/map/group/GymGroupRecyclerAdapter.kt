package kr.tangram.smartgym.ui.main.map.group

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kr.tangram.smartgym.databinding.ItemGroupListBinding


class GymGroupRecyclerAdapter: RecyclerView.Adapter<GymGroupRecyclerAdapter.ViewHolder>() {
    private val groupList: MutableList<String> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGroupListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!groupList.isNullOrEmpty()){
            val item = groupList[position]
        }
    }
    fun setList(list : MutableList<String>){
        groupList.clear()
        this.groupList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = groupList.size

    inner class ViewHolder(binding: ItemGroupListBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}