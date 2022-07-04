package kr.tangram.smartgym.di

import androidx.room.Room
import com.google.gson.GsonBuilder
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kr.tangram.smartgym.BuildConfig
import kr.tangram.smartgym.data.local.AppDatabase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private val httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

private val provideOkHttpClient = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()

private val gson = GsonBuilder().setLenient().create()

val networkModule = module {

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .client(provideOkHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "smart_gym_DB")
            .fallbackToDestructiveMigration()
            .build()
    }
}