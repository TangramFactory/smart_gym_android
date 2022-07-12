package kr.tangram.smartgym.ui.workout

import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseDialogFragment
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.data.remote.request.JumpSaveObject
import kr.tangram.smartgym.databinding.FragmentRopeSyncBinding
import kr.tangram.smartgym.util.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit


class RopeSyncFragment  : BaseDialogFragment<FragmentRopeSyncBinding, WorkOutViewModel>(R.layout.fragment_rope_sync){


    private lateinit  var historyListAdapter : HistoryListAdapter


    private var mTime = 0L
    private var mTask: TimerTask? = null
    private var mTimer: Timer? = null

    lateinit var identifier: String

    lateinit var deviceRegister: DeviceRegister

    companion object{
        fun newInstance(identifier: String) : RopeSyncFragment
        {
            val fragment = RopeSyncFragment()
            val args = Bundle()
            args.putString(Define.Extra.Identifier, identifier)
            fragment.arguments = args
            return fragment
        }
    }

    override val viewModel: WorkOutViewModel by lazy { getViewModel() }

    override fun onDestroyView() {
        super.onDestroyView()

        RxBus.get().unregister(this);

        stopTimer()

        viewModel.deleteHistory()

        var checkList = historyListAdapter.getCheckList()

        if(!checkList.isNullOrEmpty()) {
            viewModel.saveLocalJumpData(checkList)
        }

        (activity as RopeSyncActivity?)!!.finish()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        identifier = arguments?.getString(Define.Extra.Identifier, "")!!
        binding = FragmentRopeSyncBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun init()
    {

        RxBus.get().register(this);

        binding.vgSave.background = BackgroundRoundShape.fill(getString(R.string.buttonColor))
        binding.tvDelete.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        historyListAdapter = HistoryListAdapter()

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(context)
            overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
            historyListAdapter.action = { checked, position ->

                stopTimer()
                historyListAdapter.notifyCheckedChanged(checked, position)
            }
            adapter = historyListAdapter
        }

        Handler().postDelayed({
            binding.nsView.scrollTo(0, 0)
        }, 500)
    }

    override fun initListener() {

        viewModel.getDeviceRegister(identifier!!)

        viewModel.deviceRegister.observe(this)
        {
            deviceRegister = it

            binding.tvDeviceName.text = deviceRegister.name
        }

        binding.tvDelete.setOnClickListener{onDestroyView()}

        binding.rvList.setOnClickListener{ stopTimer() }
        binding.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                stopTimer()
            }
        })
    }


    override fun initLiveData()
    {
        mTime = (1000 * 10).toLong()

        mTask = object : TimerTask() {
            override fun run() {
                mTimerHandler.sendEmptyMessage(0)
            }
        }

        mTimer = Timer()
        mTimer?.schedule(mTask, 1000L, 1000L)

    }

    private val mTimerHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(mTime <= 0){
                onDestroyView()
                return
            }

            mTime -= 1000L
            binding.tvCount.text = (mTime / 1000).toString()
        }
    }

    private fun stopTimer()
    {
        if (mTimer != null) mTimer!!.cancel()
        if (mTask != null) mTask!!.cancel()
        mTimer = null
        mTask = null
    }


    @Subscribe(tags = [Tag(Define.BusEvent.HistorySync)])
    fun eventHistoryData(param : String)
    {
        val data: List<String> = param.split(":")

        val nowTime = Date().time
        val startTime = isHex(data[3]) *1000
        val endTime = isHex(data[4]) *1000

        val durationTime = startTime - endTime
        val jumpsCount = isHex(data[2])
        val avrTime = durationTime / jumpsCount.toFloat()

        val absBeforeStartTime = nowTime - startTime.toLong()
        val absBeforeEndTime = nowTime - endTime.toLong()

        var calorie = calCalorieHistory(avrTime.toLong(), Define.HardCording.Weight) * jumpsCount
        var date = getMilliSecondDate(nowTime - startTime, "MMM dd, yyyy (E) HH:mm:ss")
        var wid = System.currentTimeMillis().toString() + Random().nextInt(100)

        var jumpSaveData = JumpSaveObject.JumpSaveData((isHex(data[1]) + 1).toString(), date,
            wid, identifier, getAndroidId()!!, jumpsCount.toString(), avrTime.toString(),
            calorie.toString(), durationTime.toString(), absBeforeEndTime.toString())

        historyListAdapter.add(jumpSaveData)
    }



    class HistoryListAdapter() : RecyclerView.Adapter<HistoryHolder>() {

        var list = ArrayList<JumpSaveObject.JumpSaveData>()
        lateinit var action:(checked: Boolean, position: Int)->Unit

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
            val inflater = LayoutInflater.from(parent.context)
            return HistoryHolder(inflater, parent, action)
        }

        override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
            holder.bind(list.get(position), position)
        }

        fun isExistRow(data: JumpSaveObject.JumpSaveData) : Boolean
        {
            for(i in 0 until list.size)
            {
                if(list.get(i).row == data.row)
                    return true
            }
            return false
        }

        fun add(data: JumpSaveObject.JumpSaveData)
        {
            if(!isExistRow(data)){
                this.list.add(0, data)
                notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int = list.size

        fun notifyCheckedChanged(checked: Boolean, position: Int)
        {
            list.get(position).checked = checked
            notifyDataSetChanged()
        }

        fun getCheckList() : List<JumpSaveObject.JumpSaveData>
        {
            var checkList = ArrayList<JumpSaveObject.JumpSaveData>()

            for(i in 0 until list.size)
            {
                if(list[i].checked!!)
                {
                    checkList.add(list[i])
                }
            }

            return checkList
        }
    }

    class HistoryHolder(inflater: LayoutInflater, parent: ViewGroup, action:(checked: Boolean, position: Int)->Unit)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.row_rope_sync, parent, false) )
    {
        private var tvRow : TextView = itemView.findViewById(R.id.tv_row)
        private var tvDate : TextView = itemView.findViewById(R.id.tv_date)
        private var tvJump : TextView = itemView.findViewById(R.id.tv_jump)
        private var ivCheck : ImageView = itemView.findViewById(R.id.iv_check)
        private var layout : ViewGroup = itemView.findViewById(R.id.layout)
        private var tvDuration : TextView = itemView.findViewById(R.id.tv_duration)

        var action:(checked: Boolean, position: Int)->Unit = action

        val formatter = DecimalFormat("#,###")

        fun bind(data: JumpSaveObject.JumpSaveData, position: Int)
        {
            tvRow.setText(data.row)
            tvDate.setText(data.sdate)
            tvJump.setText(formatter.format(data.jump.toInt()))

            var duration = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(data.duration.toLong()),
                TimeUnit.MILLISECONDS.toMinutes(data.duration.toLong()) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(data.duration.toLong())), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(data.duration.toLong()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(data.duration.toLong())));

            tvDuration.setText(duration)

            if(data.checked!!){
                ivCheck.setImageResource(R.drawable.ic_checkbox_on_gray)
            } else{
                ivCheck.setImageResource(R.drawable.ic_checkbox_off_gray)
            }

            layout.setOnClickListener{
                action(!data.checked!!, position)
            }
        }
    }




}