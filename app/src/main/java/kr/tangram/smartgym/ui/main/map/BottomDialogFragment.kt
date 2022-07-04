package kr.tangram.smartgym.ui.main.map

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.tangram.smartgym.R
import kr.tangram.smartgym.data.domain.model.GymInfo
import kr.tangram.smartgym.databinding.ItemDialogButtonBinding
import kr.tangram.smartgym.databinding.ItemGymListBinding
import kr.tangram.smartgym.databinding.LayoutDialogListBinding
import kr.tangram.smartgym.ui.leftMenu.GymCreateActivity
import kr.tangram.smartgym.ui.main.map.gym.GymViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BottomDialogFragment : BottomSheetDialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(data:ArrayList<String>) = BottomDialogFragment().apply {
            arguments = Bundle().apply {
                putStringArrayList("data", data)
            }
        }
    }



    private val receiveData by lazy { requireArguments().getStringArrayList("data") }
    private val viewModel : GymViewModel by sharedViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = LayoutDialogListBinding.inflate(inflater)
        val adapter = DialogAdapter()
        binding.recyclerViewDialog.adapter = adapter
        adapter.setList(receiveData!!.toList())
        return binding.root
    }
}


class DialogAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            binding.tvBtn.text = "1"
            binding.tvBtn.setOnClickListener {
//                if (onItemClickListener != null) {
//                    onItemClickListener.onClick("")
//                }

            }
        }
    }

}