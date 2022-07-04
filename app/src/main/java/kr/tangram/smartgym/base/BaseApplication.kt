package kr.tangram.smartgym.base

import android.app.Activity
import android.app.Application
import android.content.Context
import com.lyft.kronos.AndroidClockFactory
import com.lyft.kronos.KronosClock
import android.content.Intent
import android.os.Handler
import com.orhanobut.hawk.Hawk
import kr.tangram.smartgym.di.*
import kr.tangram.smartgym.util.Define
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

        Hawk.init(context).build();

        setIsLockScreen(false)
    }

    companion object {
        var application: Application? = null
            private set
        val context: Context
            get() = application!!.applicationContext

        fun startActivityLock(activity: Activity, intent: Intent?, isFinish: Boolean) {
            if (!isLockScreen() && activity != null && intent != null) {
                releaseLockScreen()
                activity.startActivity(intent)
                if (isFinish) {
                    activity.finish()
                }
            }
        }

        fun startActivityForResultLock(activity: Activity, intent: Intent?, reqCode: Int) {
            if (!isLockScreen() && activity != null && intent != null) {
                releaseLockScreen()
                activity.startActivityForResult(intent, reqCode)
            }
        }


        fun isLockScreen(): Boolean {
            return Hawk.get(Define.AppData.ScreenLock, false)
        }


        fun releaseLockScreen() {
            setIsLockScreen(true)
            val delayHandler = Handler()
            delayHandler.postDelayed({ setIsLockScreen(false) }, 1000)
        }

        fun setIsLockScreen(isLockScreen: Boolean) {
            Hawk.put(Define.AppData.ScreenLock, isLockScreen)
        }

        val kronosClock: KronosClock
            get() = AndroidClockFactory.createKronosClock(context).apply {
                syncInBackground()
            }
    }





}