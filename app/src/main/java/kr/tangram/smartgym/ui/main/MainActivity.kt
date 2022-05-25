package kr.tangram.smartgym.ui.main

import android.text.method.ScrollingMovementMethod
import kr.tangram.smartgym.R
import kr.tangram.smartgym.base.BaseActivity
import kr.tangram.smartgym.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
    R.layout.activity_main
) {

    override val viewModel: MainViewModel by viewModel()

    override fun initLiveData()
    {
        viewModel.name.observe(this) {
            binding.apply {
            }
        }
    }



}