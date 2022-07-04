package kr.tangram.smartgym.ui.main.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.tangram.smartgym.databinding.ItemDialogButtonBinding
import kr.tangram.smartgym.databinding.LayoutDialogListBinding
import kr.tangram.smartgym.ui.main.map.gym.GymViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GymCreateBottomDialogFragment(val data:ArrayList<String>, val onItemClick : (str: String) -> Unit): BottomSheetDialogFragment(), GymCreateDialogAdapter.OnItemClickListener{
    private val viewModel : GymViewModel by sharedViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LayoutDialogListBinding.inflate(inflater)
        val adapter = GymCreateDialogAdapter()
        binding.recyclerViewDialog.adapter = adapter
        adapter.setList(data.toList())
        adapter.setOnItemClickListener(this)
        return binding.root
    }

    override fun onClick(str: String) {
        onItemClick(str)
       requireActivity().supportFragmentManager.apply {
           beginTransaction().remove(this@GymCreateBottomDialogFragment).commit()
           popBackStack()
       }
    }
}


class GymCreateDialogAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list: MutableList<String> = mutableListOf()
    private lateinit var binding: ItemDialogButtonBinding
    private lateinit var onItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onClick(str: String)
    }

    fun setList(list: List<String>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding =
            ItemDialogButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DialogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DialogViewHolder) {
            holder.bind(list[position])
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun getItemCount(): Int = list.size

    inner class DialogViewHolder(val binding: ItemDialogButtonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(string: String) {
            binding.tvBtn.text = string
            binding.tvBtn.setOnClickListener {
                Log.d("클릭","~")
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(string)
                }

            }
        }
    }

}