package kr.tangram.smartgym.ble

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder

class BluetoothService : Service() {

    private val binder = LocalBinder()
    private lateinit var smartRopeManager : SmartRopeManager

    inner class LocalBinder : Binder(){
        internal fun getService(): BluetoothService {
            return this@BluetoothService
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startRopeManager()

        return START_STICKY
    }

    fun startRopeManager()
    {
        smartRopeManager = SmartRopeManager.getInstance()
        smartRopeManager.startScanAndConnect()
        smartRopeManager.checkDisconnect()

    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}