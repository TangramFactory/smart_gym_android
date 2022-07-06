package kr.tangram.smartgym.di

import androidx.lifecycle.MutableLiveData
import kr.tangram.smartgym.data.remote.BleRestApi
import kr.tangram.smartgym.data.remote.UserRestApi
import kr.tangram.smartgym.data.remote.WorkOutRestApi
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {
    single { get<Retrofit>().create(UserRestApi::class.java) }
    single { get<Retrofit>().create(WorkOutRestApi::class.java) }
    single { get<Retrofit>().create(BleRestApi::class.java) }
    single { MutableLiveData<Int>() }
}