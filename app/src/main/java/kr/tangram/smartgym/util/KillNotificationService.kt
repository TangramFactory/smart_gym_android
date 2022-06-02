package kr.tangram.smartgym.util

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder

//
class KillNotificationService : Service() {

    class KillBinder : Binder() {
        lateinit var service:Service
//        fun KillBinder(service:Service) {
//            this.service = service
//        }
    }

    private val NOTIFICATION_ID = 1
    private lateinit var  notiManager: NotificationManager
    private val killBinder:KillBinder = KillBinder()

    override fun onBind(intent: Intent?): IBinder {
        killBinder.service = this
        return killBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        notiManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notiManager.cancel(NOTIFICATION_ID)
    }

}