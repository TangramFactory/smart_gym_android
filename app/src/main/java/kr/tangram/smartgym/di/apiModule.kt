package kr.tangram.smartgym.di

import kr.tangram.smartgym.data.remote.RestApi
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {
    single { get<Retrofit>().create(RestApi::class.java) }
}