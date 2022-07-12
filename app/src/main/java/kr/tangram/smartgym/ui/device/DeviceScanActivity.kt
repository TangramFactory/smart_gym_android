package kr.tangram.smartgym.ui.device

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.OnClick
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.ble.SmartRopeManager
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.databinding.ActivityDeviceScanBinding
import kr.tangram.smartgym.util.getNowDateFormat
import no.nordicsemi.android.support.v18.scanner.ScanResult
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeviceScanActivity  : BaseActivity<ActivityDeviceScanBinding, DeviceViewModel>(R.layout.activity_device_scan) {
    override val viewModel: DeviceViewModel by viewModel()
    private lateinit  var deviceListAdapter : DeviceListAdapter
    lateinit  var smartRopeManager : SmartRopeManager

    var aliasMap = HashMap<String, String>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityDeviceScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ButterKnife.bind(this)

        binding.header.tvTitle.text = "새로운 기기 연결"

        deviceListAdapter = DeviceListAdapter()

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(context)
            overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
            deviceListAdapter.action={
                viewModel.saveDevice(it)
            }
            adapter = deviceListAdapter
        }


        viewModel.stopScan.observe(this){
            notifyButtonChanged(false)

            var items = deviceListAdapter.getItems()

            if(!items.isNullOrEmpty()) {

                var list = ArrayList<DeviceRegister>()

                for(i in 0 until items.size){
                    var deviceRegister = DeviceRegister(items[i].scanRecord?.deviceName, true, 100, false, "",
                        getNowDateFormat("yyyyMMddHHmmss"), items[i].device.address,
                        items[i].scanRecord?.deviceName, "", "")

                    list.add(deviceRegister)
                }

                viewModel.getDeviceLoadList(list)
            }
        }

        viewModel.scanDevice.observe(this){
            deviceListAdapter.add(it)
        }

        viewModel.deviceLoadList.observe(this){

            for(i in 0 until it.size) {
                aliasMap.put(it[i].deviceIdentify, it[i].deviceAlias)
            }

            deviceListAdapter.setAliasMap(aliasMap)
            deviceListAdapter.notifyDataSetChanged()
        }
        
        onClickSearch()

    }


    private fun notifyButtonChanged(isScanning : Boolean)
    {
        if(isScanning){
            binding.layoutScan.visibility = View.VISIBLE
            binding.layoutSearch.visibility = View.GONE
        } else{
            binding.layoutScan.visibility = View.GONE
            binding.layoutSearch.visibility = View.VISIBLE
        }
    }


    @OnClick(R.id.menu_back)
    fun onClickBack()
    {
        finish()
    }


    @OnClick(R.id.menu_right)
    fun onClickRight()
    {

    }

    @OnClick(R.id.layout_search)
    fun onClickSearch()
    {
        notifyButtonChanged(true)
        viewModel.startScan()
    }

    @OnClick(R.id.layout_scan)
    fun onClickScan()
    {
        notifyButtonChanged(false)
        viewModel.stopScan()
    }


    class DeviceListAdapter() : RecyclerView.Adapter<DeviceViewHolder>() {

        var deivceRegisterList = ArrayList<DeviceRegister>()
        var list = ArrayList<ScanResult>()
        lateinit var action:(scanResult: ScanResult)->Unit
        var alias = HashMap<String, String>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return DeviceViewHolder(inflater, parent, action)
        }

        override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
            var key = list.get(position).device.address.toString()
            var value = if(!alias.get(key).isNullOrEmpty()) alias.get(key) else ""
            holder.bind(list.get(position), value!!)
        }

        fun addList(list : ArrayList<ScanResult>)
        {
            this.list.addAll(list)
            notifyDataSetChanged()
        }

        fun addDeviceRegisterList(list: List<DeviceRegister>)
        {
            deivceRegisterList.clear()
            deivceRegisterList.addAll(list)
        }


        fun add(scanResult: ScanResult)
        {
            if(!isExistDevice(scanResult))
            {
                this.list.add(scanResult)
                notifyDataSetChanged()
            }
        }

        fun setAliasMap(map : HashMap<String, String>){
            alias = map
        }

        fun getItems() : ArrayList<ScanResult>{
            return list
        }

        fun isExistDevice(scanResult: ScanResult) : Boolean
        {
            // 스캔목록에 검사
            for(i in 0 until list.size)
            {
                if(scanResult.device.address == list.get(i).device.address){
                    return true
                }
            }

            // DB에 저장된 목록 검사
            for(i in 0 until deivceRegisterList.size)
            {
                if(scanResult.device.address == deivceRegisterList.get(i).identifier){
                    return true
                }
            }

            return false
        }

        override fun getItemCount(): Int = list.size

    }

    class DeviceViewHolder(inflater: LayoutInflater, parent: ViewGroup, action:(scanResult: ScanResult)->Unit)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.row_my_device, parent, false) )
    {
        private var vgMore : ViewGroup = itemView.findViewById(R.id.vg_more)
        private var ivPic : ImageView = itemView.findViewById(R.id.iv_pic)
        private var tvName : TextView = itemView.findViewById(R.id.tv_name)
        private var tvStatus : TextView = itemView.findViewById(R.id.tv_status)
        private var layout : ViewGroup = itemView.findViewById(R.id.layout)
        private var ivBattery : ImageView = itemView.findViewById(R.id.iv_battery)

        var action:(scanResult: ScanResult)->Unit = action

        fun bind(scanResult: ScanResult, alias: String)
        {
            vgMore.visibility = View.GONE
            tvStatus.visibility = View.GONE
            ivBattery.visibility = View.GONE

            tvName.text = if(!alias.isNullOrEmpty()) alias else scanResult.scanRecord?.deviceName.toString()

            layout.setOnClickListener{
                action(scanResult)
            }

        }
    }

}