package kr.tangram.smartgym.ble

import android.bluetooth.*
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import com.hwangjr.rxbus.RxBus
import io.reactivex.disposables.Disposable
import kr.tangram.smartgym.base.BaseApplication
import kr.tangram.smartgym.data.repository.DeviceRegisterRepository
import kr.tangram.smartgym.util.Define
import no.nordicsemi.android.support.v18.scanner.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.charset.Charset
import java.util.*


class SmartRopeManager() : KoinComponent {

    val compositeDisposable = io.reactivex.disposables.CompositeDisposable()
    fun addDisposable(disposable: Disposable) = compositeDisposable.add(disposable)

    val deviceRegisterRepository: DeviceRegisterRepository by inject()

    var uuidRookieGatt: UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e") // GATT 연결하는거 --- 루키꺼
    var uuidSmartRopeGatt: UUID = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb") // GATT 연결하는거 --- LED / PURE

    var uuidSmartRopeTx: UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e") // 보내는거
    var uuidSmartRopeRx: UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e") // 받는거

    private var gattList = ArrayList<BluetoothGatt>()

    private var scanning = false
    private var scanAndConnecting = false
    private val handler = Handler()

    private val SCAN_DURATION: Long = (3 * 1000).toLong()

    lateinit var onFound:(scanResult : ScanResult)->Unit
    lateinit var onStopScan:()->Unit
    lateinit var onCountJump:(jumpCount : Int, time_gap :Long)->Unit
    lateinit var onHistory:(identifier: String)->Unit

    lateinit var historyGatt : BluetoothGatt

    var releaseList = ArrayList<String>()

    private var writeCharacteristic:BluetoothGattCharacteristic?=null

    private var disconnectMap = HashMap<String, Long>()

    companion object {
        @Volatile private var instance: SmartRopeManager? = null

        @JvmStatic fun getInstance(): SmartRopeManager =
            instance ?: synchronized(this) {
                instance ?: SmartRopeManager().also {
                    instance = it
                }
            }
    }


    public fun startScan()
    {
        stopScan()

        val scanner: BluetoothLeScannerCompat = BluetoothLeScannerCompat.getScanner()
        val settings = ScanSettings.Builder()
            .setLegacy(false)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(1000)
            .setUseHardwareBatchingIfSupported(false).build()
        val filters: MutableList<ScanFilter> = ArrayList<ScanFilter>()
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(uuidRookieGatt)).build())
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(uuidSmartRopeGatt)).build())
        scanner.startScan(filters, settings, scanCallback)

        scanning = true
        handler.postDelayed(Runnable {
            if (scanning) {
                stopScan()
                onStopScan()
            }
        }, SCAN_DURATION)
    }


    public fun startScanAndConnect()
    {
        stopScanAndConnect()

        val scanner: BluetoothLeScannerCompat = BluetoothLeScannerCompat.getScanner()
        val settings = ScanSettings.Builder()
            .setLegacy(false)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(1000)
            .setUseHardwareBatchingIfSupported(false).build()
        val filters: MutableList<ScanFilter> = ArrayList<ScanFilter>()
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(uuidRookieGatt)).build())
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(uuidSmartRopeGatt)).build())
        scanner.startScan(filters, settings, scanAndConnectCallback)

        scanAndConnecting = true
    }

    lateinit  var  connectHandler : Handler
    lateinit var connectRunnable: Runnable

    public fun checkDisconnect()
    {
        connectHandler = Handler()
        connectRunnable = Runnable {

            for(i in 0 until gattList.size)
            {
                var lastTime = disconnectMap.get(gattList.get(i).device.address)
                val timeGap = Date().time - lastTime!!

                // 10분동안 작동이 없으면 끊는다
                if((60 * 1000 * 10) < timeGap) {
                    Log.e("disconnect", timeGap.toString())

                    disconnectMap.put(gattList.get(i).device.address, Date().time)
                    disconnect(gattList.get(i).device.address)
                }
            }

            connectHandler.postDelayed(connectRunnable!!, 10000)
        }

        connectHandler.postDelayed(connectRunnable!!, 1000)
    }

    fun stopScanning() {
        stopScan()
//        stopScanAndConnect()
    }

    fun stopScan() {
        if (scanning) {
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.stopScan(scanCallback)
            scanning = false
        }
    }

    fun stopScanAndConnect() {
        if (scanAndConnecting) {
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.stopScan(scanAndConnectCallback)
            scanAndConnecting = false
        }
    }


    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {}
        override fun onScanFailed(errorCode: Int) {}

        override fun onBatchScanResults(results: List<ScanResult>) {
            if (results != null) {
                for (i in results.indices) {
                    onFound(results[i])
                }
            }
        }
    }


    private val scanAndConnectCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {}
        override fun onScanFailed(errorCode: Int) {}

        override fun onBatchScanResults(results: List<ScanResult>) {
            if (results != null) {
                for (i in results.indices) {
                    deviceRegisterRepository.getDeviceList(results[i].device.address).doOnSuccess {
                        if(!it.isNullOrEmpty() && it[0].auto!! && !isExistRelease(it[0].identifier!!)){

                            var lastTime = if(disconnectMap.get(results[i].device.address) == null) Date().time else disconnectMap.get(results[i].device.address)
                            val timeGap = Date().time - lastTime!!

                            if(0 < timeGap && timeGap < (30 * 1000)) {
                                Log.e("find device", "false:" + timeGap.toString())
                            } else{
                                Log.e("find device", "true")
                                connect(results[i].device)
                            }
                        }
                    }.subscribe()

                }
            }
        }
    }


    fun isExistRelease(identifier: String) : Boolean
    {
        for(i in 0 until releaseList.size)
        {
            if (identifier == releaseList.get(i))
            {
                return true
            }
        }
        return false
    }


    fun addRelease(identifier: String)
    {
        if(!isExistRelease(identifier))
        {
            releaseList.add(identifier)
        }
    }

    fun removeRelease(identifier: String)
    {
        for(i in 0 until releaseList.size)
        {
            if(identifier == releaseList.get(i))
            {
                releaseList.removeAt(i)
                break
            }
        }
    }

    fun clearRelease()
    {
        releaseList.clear()
    }

    fun disconnect(identifier: String)
    {
        for(i in 0 until gattList.size)
        {
            if(identifier == gattList[i].device.address){
                gattList[i].disconnect()
            }
        }
    }


    fun connect(device :BluetoothDevice){
        device.connectGatt(BaseApplication.context, true, mGattCallback)
    }


    fun addGatt(gatt: BluetoothGatt)
    {
        for(i in 0 until gattList.size)
        {
            if(gatt.device.address == gattList[i].device.address){
                return
            }
        }

        disconnectMap.put(gatt.device.address, Date().time)

        gattList.add(gatt)
    }


    fun removeGatt(gatt: BluetoothGatt)
    {
        for(i in 0 until gattList.size)
        {
            if(gatt.device.address == gattList[i].device.address){
                gattList.removeAt(i)
                break
            }
        }
    }



    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                if(!isExistRelease(gatt.device.address)) {
                    gatt.discoverServices()
                    addGatt(gatt)
                    deviceRegisterRepository.updateDeviceConnect(true, gatt.device.address)
                    sendBus(Define.BusEvent.DeviceState, "")
                } else{
                    gatt.disconnect()
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gatt.disconnect()
                removeGatt(gatt)
                deviceRegisterRepository.updateDeviceConnect(false, gatt.device.address)
                sendBus(Define.BusEvent.DeviceState, "")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                val serviceList = gatt!!.services
                serviceList?.forEach { gattService ->

                    val gattCharacteristics = gattService.characteristics
                    gattCharacteristics.forEach { gattCharacteristic ->
                        // -- 문자열 읽기쓰기 서비스 바인딩

                        if (gattCharacteristic.uuid == uuidSmartRopeRx)
                        {
                            if( !gatt!!.setCharacteristicNotification(gattCharacteristic,true) ) {
                                Log.e("discovered", "stop1")
                                return
                            }

                            for (des in gattCharacteristic.descriptors) {
                                if (null != des) {
                                    des.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                    gatt!!.writeDescriptor(des)
                                }
                            }

                        }
                        if (gattCharacteristic.uuid == uuidSmartRopeTx)
                        {
                            writeCharacteristic = gattCharacteristic
                        }

                    }
                }

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        write("HELLO?", gatt)
                    }
                }, 300)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        write("HTC?", gatt)
                    }
                }, 600)
            }
        }


        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {

            var value = characteristic.getStringValue(0).toString().replace(";", "")
            val data: List<String> = value.split(":")

            var test1 = data[0]
            var test2 = data[1]
            Log.e(test1, test2)

             when(data[0])
            {
                "BAT" -> {
                    deviceRegisterRepository.updateDeviceBattery(data[1].toInt(), gatt.device.address)
                    sendBus(Define.BusEvent.DeviceState, "")
                }
                "VER" -> {
                    deviceRegisterRepository.updateDeviceVersion(data[1].toInt(), gatt.device.address)
                    sendBus(Define.BusEvent.DeviceState, "")
                }
                "CNT" -> {
                    disconnectMap.put(gatt.device.address, Date().time)
                     onCountJump(data[1].toInt(), data[2].toLong())
                }
                "HCOUNT" -> {
                }
                "HTC" -> {
                    val totalHistory = try { data[1].toInt() } catch (e: Exception) { 0 }
                    if (totalHistory > 0) {

                        historyGatt = gatt

                        onHistory(gatt.device.address)
                        getHistoryData(totalHistory,gatt)
                    }
                }
                "H" -> {
                    sendBus(Define.BusEvent.HistorySync, value!!)
                }

                else -> {

                }
            }
        }

        override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }


        override fun onReliableWriteCompleted(gatt: BluetoothGatt, status: Int) {
            Log.e("T", "[IN]onReliableWriteCompleted: " + String.format("Status=0x%02X", status))
        }
    }


    fun write(data: String?, gatt: BluetoothGatt)
    {
        if (data != null && data.isNotEmpty())
        {
            var msg = data.toByteArray(Charset.forName("UTF-8"))

            if (writeCharacteristic == null || msg == null || msg.size == 0)
            {
                return
            }
            writeCharacteristic?.value = msg
            gatt!!.writeCharacteristic(writeCharacteristic)
        }
    }


    private fun getHistoryData(totalHistory: Int, gatt: BluetoothGatt) {
        //
        var i = 1.0f
        val phase = 100L
        var percent: Float
        val historyHandler = Handler(Looper.getMainLooper())
        val historyRunnable = object : Runnable {
            override fun run() {
//                if (ropeType.equals("S")){
                write("HTD:" + (i - 1.0f), gatt!!)
                percent = i / totalHistory
                if (percent == 1.0f) {

                }
                if (i < totalHistory) {
                    historyHandler.postDelayed(this, phase)
                    i++
                }
            }
        }
        //
        historyHandler.postDelayed(historyRunnable, phase)

    }

    fun sendBus(tag:String, data:String)
    {
        RxBus.get().post(tag, data);
    }

    fun deleteHistory()
    {
        if(historyGatt != null)
        {
//            write("HTR!", historyGatt)
        }
    }
}