package kr.tangram.smartgym.base

import android.app.Application
import android.content.Context
import kr.tangram.smartgym.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    val tag = javaClass.name



    override fun onCreate() {
        super.onCreate()
        application = this
        startKoin {
            androidContext(this@BaseApplication)
            modules(
                appModule,
                activityModule,
                fragmentModule,
                networkModule,
                apiModule,
            )
        }
    }

    companion object {
        var application: Application? = null
            private set
        val context: Context
            get() = application!!.applicationContext

    }
}