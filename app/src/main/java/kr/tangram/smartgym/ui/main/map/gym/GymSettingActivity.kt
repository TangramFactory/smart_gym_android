package kr.tangram.smartgym.ui.main.map.gym

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityGymSettingBinding
import org.koin.androidx.viewmodel.ext.android.getViewModel

class GymSettingActivity :BaseActivity<ActivityGymSettingBinding, GymViewModel>(R.layout.activity_gym_setting) {
    override val viewModel: GymViewModel by lazy { getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGymSettingBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}