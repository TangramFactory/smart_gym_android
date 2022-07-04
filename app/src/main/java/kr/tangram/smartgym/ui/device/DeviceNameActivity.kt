package kr.tangram.smartgym.ui.device

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import butterknife.ButterKnife
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityDeviceNameBinding
import kr.tangram.smartgym.util.BackgroundRoundShape

class DeviceNameActivity  : BaseActivity<ActivityDeviceNameBinding, DeviceViewModel>(R.layout.activity_device_name) {
    override val viewModel: DeviceViewModel by viewModels()
    private lateinit var deviceListAdapter: DeviceManagerActivity.DeviceListAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeviceNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ButterKnife.bind(this)

        binding.header.tvTitle.text = "기기 정보 수정"

        binding.btnSave.background = BackgroundRoundShape.fill(getString(R.string.buttonColor))
    }

}