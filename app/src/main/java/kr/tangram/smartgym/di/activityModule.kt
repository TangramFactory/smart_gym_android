package kr.tangram.smartgym.di

import android.app.Application
import kr.tangram.smartgym.ui.challenge.ChallengeViewModel
import kr.tangram.smartgym.ui.device.DeviceViewModel
import kr.tangram.smartgym.ui.intro.IntroViewModel
import kr.tangram.smartgym.ui.junior.JuniorViewModel
import kr.tangram.smartgym.ui.login.LoginViewModel
import kr.tangram.smartgym.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val activityModule = module {

    viewModel { MainViewModel()}
    viewModel { LoginViewModel()}
    viewModel { ChallengeViewModel() }
    viewModel { JuniorViewModel() }
    viewModel { DeviceViewModel() }
    viewModel { IntroViewModel() }
}