package kr.tangram.smartgym.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityLoginCompleteBinding
import kr.tangram.smartgym.ui.login.junior.JuniorActivity
import kr.tangram.smartgym.ui.main.MainActivity
import kr.tangram.smartgym.UserViewModel
import kr.tangram.smartgym.ui.device.DeviceManagerActivity
import kr.tangram.smartgym.ui.deviceConnect.DeviceDiscoverActivity
import kr.tangram.smartgym.ui.leftMenu.ProfileSettingActivity
import kr.tangram.smartgym.util.BackgroundRoundShape
import org.koin.androidx.viewmodel.ext.android.getViewModel

class LoginCompleteActivity : BaseActivity<ActivityLoginCompleteBinding, UserViewModel>(R.layout.activity_login_complete) {
    override val viewModel: UserViewModel by lazy { getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner=this
        binding.viewModel = viewModel

        binding.btnConfirm.background =  BackgroundRoundShape.fill(getString(R.string.buttonColor))
        binding.btnConfirm.setOnClickListener { startActivity(Intent(this, MainActivity::class.java) ) }

        binding.layoutProfile.isClickable = true
        binding.layoutProfile.setOnClickListener { startActivity(Intent(this, ProfileSettingActivity::class.java)) }

        binding.layoutJuniorAccount.isClickable = true
        binding.layoutJuniorAccount.setOnClickListener { startActivity(Intent(this, JuniorActivity::class.java)) }

        binding.layoutConnectDevice.isClickable = true
        binding.layoutConnectDevice.setOnClickListener { startActivity(Intent(this, DeviceManagerActivity::class.java)) }

        if (viewModel.info.value?.userJuniorYn=="Y"){
            binding.layoutJuniorAccount.visibility = View.GONE
            binding.view2.visibility = View.GONE
        }

        Glide.with(this)
            .load(viewModel.getProfileImagePath())
            .error(R.drawable.ic_default_profile)
            .into(binding.imgProfile)
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.refreshData()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java) )
    }
}