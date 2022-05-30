package kr.tangram.smartgym.di

import ContributorRepository
import kr.tangram.smartgym.util.FireBaseEmailLogin
import org.koin.android.ext.koin.androidContext
import TableNameRepository
import org.koin.dsl.module

val appModule = module {
    single {
        FireBaseEmailLogin(androidContext())
    }

    factory {
        TableNameRepository(get(), get())
    }
}