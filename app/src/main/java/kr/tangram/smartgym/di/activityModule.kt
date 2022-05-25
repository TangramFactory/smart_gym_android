package kr.tangram.smartgym.di

import kr.tangram.smartgym.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val activityModule = module {

    viewModel {
        MainViewModel()
    }
}