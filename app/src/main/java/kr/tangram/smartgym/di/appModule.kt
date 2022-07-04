package kr.tangram.smartgym.di

import com.orhanobut.hawk.Hawk
import kr.tangram.smartgym.util.FireBaseEmailLogin
import org.koin.android.ext.koin.androidContext
import kr.tangram.smartgym.data.repository.DeviceRegisterRepository
import kr.tangram.smartgym.data.repository.UserRepository
import kr.tangram.smartgym.data.repository.WorkOutRepository
import org.koin.dsl.module

val appModule = module {
    single {
        FireBaseEmailLogin(androidContext())
    }

    single { Hawk.init(androidContext()).build() }

    
    factory { DeviceRegisterRepository(get(), get()) }

    factory { UserRepository(get(), get()) }

    single { WorkOutRepository(get(), get())}
}