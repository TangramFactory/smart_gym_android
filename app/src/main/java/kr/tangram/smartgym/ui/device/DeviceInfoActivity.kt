package kr.tangram.smartgym.ui.device

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import butterknife.ButterKnife
import butterknife.OnClick
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.base.BaseApplication
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.databinding.ActivityDeviceInfoBinding
import kr.tangram.smartgym.util.Define
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeviceInfoActivity  : BaseActivity<ActivityDeviceInfoBinding, DeviceViewModel>(R.layout.activity_device_info) {
    override val viewModel: DeviceViewModel by viewModel()
    private lateinit var deviceListAdapter: DeviceManagerActivity.DeviceListAdapter
    lateinit var deviceIdentifier : String
    lateinit var deviceRegister: DeviceRegister

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deviceIdentifier = intent.getStringExtra(Define.Extra.Identifier)!!

        binding = ActivityDeviceInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RxBus.get().register(this);
        ButterKnife.bind(this)

        binding.header.tvTitle.text = "기기 정보"

        binding.tvRelease.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.tvDelete.paintFlags = Paint.UNDERLINE_TEXT_FLAG



        viewModel.deviceRegister.observe(this){

            deviceRegister = it

            binding.tvDeviceName.text = it.name
            binding.tvBattery.text = it.battery.toString() + "%"
            binding.tvVersion.text = it.option


            if(it.connect!!) {
                binding.tvConnect.text = "연결됨"
                binding.tvRelease.text = "연결 해제"
                binding.tvBattery.setTextColor(resources.getColor(R.color.color_both_1))
                when{
                    90 < deviceRegister.battery!! -> binding.ivBattery.setImageResource(R.drawable.ic_battery_large_100)
                    50 < deviceRegister.battery!! -> binding.ivBattery.setImageResource(R.drawable.ic_battery_large_75)
                    25 < deviceRegister.battery!! -> binding.ivBattery.setImageResource(R.drawable.ic_battery_large_50)
                    10 < deviceRegister.battery!! -> binding.ivBattery.setImageResource(R.drawable.ic_battery_large_25)
                    else -> binding.ivBattery.setImageResource(R.drawable.ic_battery_large_0)
                }
            }
            else {
                binding.tvConnect.text = "연결안됨"
                binding.tvRelease.text = "연결 하기"
                binding.ivBattery.setImageResource(R.drawable.ic_battery_large_disconnect)
                binding.tvBattery.setTextColor(resources.getColor(R.color.color_both_7))
            }

            binding.switchAuto.isChecked = it.auto!!
        }

        viewModel.deleteDevice.observe(this){
            finish()
        }

        getDeviceRegister()
    }

    @OnClick(R.id.switch_auto)
    fun onClickAuto()
    {
        if(deviceRegister != null)
        {
            viewModel.updateDeviceRegister(deviceRegister.identifier!!, if(binding.switchAuto.isChecked) true else false)
        }
    }

    @OnClick(R.id.layout_update)
    fun onClickUpdate()
    {
        var intent = Intent(this@DeviceInfoActivity, DeviceNameActivity::class.java).apply {
            putExtra(Define.Extra.Identifier, deviceIdentifier)
        }

        BaseApplication.startActivityLock(this@DeviceInfoActivity, intent, false)

    }

    @OnClick(R.id.tv_release)
    fun onClickRelease()
    {
        if(deviceRegister.connect!!) {
            viewModel.addRelease(deviceRegister.identifier!!)
            viewModel.disConnect(deviceRegister.identifier!!)
        }
        else {
            viewModel.removeRelease(deviceRegister.identifier!!)
        }
    }

    @OnClick(R.id.tv_delete)
    fun onClickDelete()
    {
        viewModel.disConnect(deviceRegister.identifier!!)
        viewModel.deleteDeviceRegister(deviceRegister.identifier!!)
    }


    @Subscribe(tags = [Tag(Define.BusEvent.DeviceState)])
    fun eventDeviceState(param : String)
    {
        getDeviceRegister()
    }

    fun getDeviceRegister(){
        viewModel.getDeviceRegister(deviceIdentifier!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.get().unregister(this);
    }
}