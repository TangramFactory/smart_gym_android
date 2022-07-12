package kr.tangram.smartgym.ui.device

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import butterknife.ButterKnife
import butterknife.OnClick
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.base.BaseApplication
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.databinding.ActivityDeviceNameBinding
import kr.tangram.smartgym.util.BackgroundRoundShape
import kr.tangram.smartgym.util.Define

class DeviceNameActivity  : BaseActivity<ActivityDeviceNameBinding, DeviceViewModel>(R.layout.activity_device_name) {
    override val viewModel: DeviceViewModel by viewModels()
    private lateinit var deviceListAdapter: DeviceManagerActivity.DeviceListAdapter

    lateinit var deviceIdentifier : String

    lateinit var deviceRegister: DeviceRegister

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deviceIdentifier = intent.getStringExtra(Define.Extra.Identifier)!!

        binding = ActivityDeviceNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ButterKnife.bind(this)

        binding.header.tvTitle.text = "기기 정보 수정"

        binding.btnSave.background = BackgroundRoundShape.fill(getString(R.string.buttonColor))

        viewModel.deviceRegister.observe(this){

            deviceRegister = it

            binding.etDeviceName.setText(it.name)
            binding.tvDeviceName.text = it.name
        }

        viewModel.updateDeviceAlias.observe(this){
            finish()
        }

        viewModel.getDeviceRegister(deviceIdentifier!!)
    }


    @OnClick(R.id.btn_save)
    fun onClickDeviceName()
    {
        if(binding.etDeviceName.text.toString().trim().isNullOrEmpty()) {
            Toast.makeText(BaseApplication.context, "기기이름을 입력하세요", Toast.LENGTH_LONG).show()
        } else{
            viewModel.updateDeviceAlas(deviceRegister, binding.etDeviceName.text.toString())
        }
    }

}