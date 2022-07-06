package kr.tangram.smartgym.di

import kr.tangram.smartgym.ui.main.challenge.ChallengeViewModel
import android.app.Application
import kr.tangram.smartgym.ui.device.DeviceViewModel
import kr.tangram.smartgym.ui.intro.IntroViewModel
import kr.tangram.smartgym.ui.login.LoginViewModel
import kr.tangram.smartgym.UserViewModel
import kr.tangram.smartgym.base.BaseViewModel
import kr.tangram.smartgym.ui.login.junior.JuniorViewModel
import kr.tangram.smartgym.ui.main.map.gym.GymViewModel
import kr.tangram.smartgym.ui.workout.WorkOutViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val activityModule = module {
    viewModel { UserViewModel() }
    viewModel { LoginViewModel() }
    viewModel { ChallengeViewModel() }
    viewModel { JuniorViewModel() }
    viewModel { DeviceViewModel() }
    viewModel { IntroViewModel() }
    viewModel { WorkOutViewModel(get()) }
    viewModel { GymViewModel() }
    viewModel { BaseViewModel() }
}