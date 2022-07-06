package kr.tangram.smartgym.ui.device

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.base.BaseApplication
import kr.tangram.smartgym.ble.BluetoothService
import kr.tangram.smartgym.ble.SmartRopeManager
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.databinding.ActivityDeviceManagerBinding
import kr.tangram.smartgym.util.Define
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeviceManagerActivity  : BaseActivity<ActivityDeviceManagerBinding, DeviceViewModel>(R.layout.activity_device_manager) {
    override val viewModel: DeviceViewModel by viewModel()
    private lateinit  var deviceListAdapter : DeviceListAdapter
    var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val PERMISSIONS_S_ABOVE = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val REQUEST_ALL_PERMISSION = 2

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeviceManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RxBus.get().register(this);
        ButterKnife.bind(this)

        binding.header.tvTitle.text = "기기 관리"
        deviceListAdapter = DeviceListAdapter()

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(context)
            overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
            deviceListAdapter.actionModify={

                var intent = Intent(this@DeviceManagerActivity, DeviceInfoActivity::class.java).apply {
                    putExtra(Define.Extra.Identifier, it)
                }

                BaseApplication.startActivityLock(this@DeviceManagerActivity, intent, false)
            }
            deviceListAdapter.actionConnect={

            }
            adapter = deviceListAdapter
        }

        viewModel.myDeviceList.observe(this){
            deviceListAdapter.addList(it)

            viewModel.getDeviceLoadList(it)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            if (!hasPermissions(this, PERMISSIONS_S_ABOVE)) {
                requestPermissions(PERMISSIONS_S_ABOVE, REQUEST_ALL_PERMISSION)
            } else{
            }
        }else{
            if (!hasPermissions(this, PERMISSIONS)) {
                requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
            } else{
            }
        }

        var intent = Intent(getApplicationContext(), BluetoothService::class.java)
        startService(intent)


        getDeviceRegisterList()
    }


    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearRelease()
        RxBus.get().unregister(this);
    }

    override fun onResume() {
        super.onResume()

        bluetoothAdapter?.let {
            binding.switchBluetooth.isChecked = it.isEnabled
        }

    }

    @Subscribe(tags = [Tag(Define.BusEvent.DeviceState)])
    fun eventDeviceState(param : String)
    {
        getDeviceRegisterList()
    }


    fun getDeviceRegisterList()
    {
        viewModel.getDeviceRegisterList()
    }

    @OnClick(R.id.switch_bluetooth)
    fun onClickBluetooth()
    {
        setBluetoothMode()
    }


    fun setBluetoothMode()
    {
        if (bluetoothAdapter?.isEnabled == false) { // 블루투스 꺼져 있으면 블루투스 활성화
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        } else{ // 블루투스 켜져있으면 블루투스 비활성화
            bluetoothAdapter?.disable()
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
        var intent = Intent(this, DeviceScanActivity::class.java)
        BaseApplication.startActivityLock(this, intent, false)
    }

    class DeviceListAdapter() : RecyclerView.Adapter<DeviceViewHolder>() {

        var list = ArrayList<DeviceRegister>()
        lateinit var actionConnect:()->Unit
        lateinit var actionModify:(identifier: String)->Unit

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {

            val inflater = LayoutInflater.from(parent.context)
            return DeviceViewHolder(inflater, parent, actionConnect, actionModify)
        }

        override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
            holder.bind(list.get(position))
        }

        fun addList(list : List<DeviceRegister>)
        {
            this.list.clear()
            this.list.addAll(list)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = list.size
    }


    class DeviceViewHolder( inflater: LayoutInflater, parent: ViewGroup, actionConnect:()->Unit, actionModify:(identifier: String)->Unit) : RecyclerView.ViewHolder(inflater.inflate(R.layout.row_my_device, parent, false) )
    {
        private var vgMore : ViewGroup = itemView.findViewById(R.id.vg_more)
        private var ivPic : ImageView = itemView.findViewById(R.id.iv_pic)
        private var tvName : TextView = itemView.findViewById(R.id.tv_name)
        private var tvStatus : TextView = itemView.findViewById(R.id.tv_status)
        private var ivBattery : ImageView = itemView.findViewById(R.id.iv_battery)

        var actionConnect:()->Unit = actionConnect
        var actionModify:(identifier: String)->Unit = actionModify

        fun bind(deviceRegister: DeviceRegister)
        {
            tvName.text = deviceRegister.name

            if(deviceRegister.connect!!) {
                tvStatus.text = "연결됨"
                tvStatus.setTextColor(BaseApplication.context.resources.getColor(R.color.color_1))

                when{
                    90 < deviceRegister.battery!! -> ivBattery.setImageResource(R.drawable.ic_battery_small_100)
                    50 < deviceRegister.battery!! -> ivBattery.setImageResource(R.drawable.ic_battery_small_75)
                    25 < deviceRegister.battery!! -> ivBattery.setImageResource(R.drawable.ic_battery_small_50)
                    10 < deviceRegister.battery!! -> ivBattery.setImageResource(R.drawable.ic_battery_small_25)
                    else -> ivBattery.setImageResource(R.drawable.ic_battery_small_0)
                }

            } else{
                tvStatus.text = "연결안됨"
                tvStatus.setTextColor(BaseApplication.context.resources.getColor(R.color.color_both_6))
                ivBattery.setImageResource(R.drawable.ic_battery_small_disconnect)
            }


            vgMore.setOnClickListener{
                actionModify(deviceRegister.identifier!!)
            }

        }
    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ALL_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    var smartRopeManager = SmartRopeManager.getInstance().apply {
                        startScan()
                    }

                } else {
                    requestPermissions(permissions, REQUEST_ALL_PERMISSION)
                    Toast.makeText(this, "Permissions must be granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}