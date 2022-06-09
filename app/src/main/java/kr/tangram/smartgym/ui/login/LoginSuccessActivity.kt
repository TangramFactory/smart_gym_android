package kr.tangram.smartgym.ui.login

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityLoginSuccessBinding
import kr.tangram.smartgym.ui.main.MainActivity
import kr.tangram.smartgym.util.BackgroundRoundShape
import org.koin.androidx.viewmodel.ext.android.getViewModel

class LoginSuccessActivity : BaseActivity<ActivityLoginSuccessBinding, LoginViewModel>(R.layout.activity_login_success) {
    override val viewModel: LoginViewModel by lazy { getViewModel() }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirm.background =  BackgroundRoundShape.fill("#3BA1FF")
        binding.btnConfirm.setOnClickListener { startActivity(Intent(this, MainActivity::class.java) ) }

        binding.layoutProfile.isClickable = true
        binding.layoutProfile.setOnClickListener {  }

        binding.layoutJuniorAccount.isClickable = true
        binding.layoutJuniorAccount.setOnClickListener {  }

        binding.layoutConnectDevice.isClickable = true
        binding.layoutConnectDevice.setOnClickListener {  }
    }



}