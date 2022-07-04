package kr.tangram.smartgym.ui.main.map.gym

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.data.domain.model.GymInfo
import kr.tangram.smartgym.databinding.ActivityGymListBinding
import kr.tangram.smartgym.databinding.ItemGymListBinding
import org.koin.androidx.viewmodel.ext.android.getViewModel

class GymListActivity : BaseActivity<ActivityGymListBinding, GymViewModel>(R.layout.activity_gym_list){

    override val viewModel: GymViewModel by lazy { getViewModel() }
    private lateinit var gymRecyclerAdapter: GymRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGymListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_header_arrow)
        supportActionBar?.title = "짐 리스트"

        gymRecyclerAdapter = GymRecyclerAdapter(viewModel.gymDataList.value!!)
        binding.recyclerViewGym.adapter = gymRecyclerAdapter
        binding.recyclerViewGym.layoutManager = LinearLayoutManager(this)
        gymRecyclerAdapter.setOnItemClickListener(object : GymRecyclerAdapter.OnItemClickListener {
            override fun onClick(gymInfo: GymInfo) {
                val intent = Intent(this@GymListActivity, GymActivity::class.java)
                intent.putExtra("gymInfo", gymInfo)
                startActivity(intent)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




    companion object{
        @BindingAdapter("bind_gymInfoList")
        @JvmStatic
        fun bindMemoList(recyclerView:RecyclerView, list: List<GymInfo>?){
            if (list.isNullOrEmpty()) return
            if(recyclerView.adapter == null){
                val lm = LinearLayoutManager(recyclerView.context)
                val adapter = GymRecyclerAdapter(list)
                recyclerView.layoutManager = lm
                recyclerView.adapter = adapter
            }
            (recyclerView.adapter as GymRecyclerAdapter).setList(list)
            recyclerView.adapter?.notifyDataSetChanged()
        }

        @BindingAdapter("bind_float_to_text")
        @JvmStatic
        fun bindText(textView: TextView, distance:Float){
            textView.text = distance.toString()
        }

        @BindingAdapter("bind_int_to_text")
        @JvmStatic
        fun bindText(textView: TextView, userCount:Int){
            textView.text = userCount.toString()
        }
    }

}

class GymRecyclerAdapter(private var list: List<GymInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private lateinit var binding: ItemGymListBinding
    private lateinit var onItemClickListener : OnItemClickListener

    interface OnItemClickListener {
        fun onClick(position: GymInfo)
    }

    fun setList(list: List<GymInfo>){
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemGymListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GymViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GymViewHolder) {
            holder.bind(list[position])
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener = onItemClickListener
    }

    override fun getItemCount(): Int = list.size

    inner class GymViewHolder(val binding: ItemGymListBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(gymInfo: GymInfo){
            binding.model = gymInfo
            binding.layoutGymListItem.setOnClickListener {
                onItemClickListener.onClick(gymInfo)
            }
        }
    }

}