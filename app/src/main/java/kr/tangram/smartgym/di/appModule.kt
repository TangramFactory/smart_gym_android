package kr.tangram.smartgym.di

import ContributorRepository
import kr.tangram.smartgym.util.FireBaseEmailLogin
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single {
        FireBaseEmailLogin(androidContext())
    }

    factory {
        ContributorRepository(get())
    }
}