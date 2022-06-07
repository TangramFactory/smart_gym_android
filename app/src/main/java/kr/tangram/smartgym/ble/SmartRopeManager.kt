package kr.tangram.smartgym.ble

import android.bluetooth.*
import android.content.Context
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import com.orhanobut.hawk.Hawk
import kr.tangram.smartgym.base.BaseApplication
import kr.tangram.smartgym.util.Define
import no.nordicsemi.android.support.v18.scanner.*
import java.lang.reflect.Method
import java.util.*


class SmartRopeManager {

    var uuidRookieGatt: UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e") // GATT 연결하는거 --- 루키꺼
    var uuidSmartRopeGatt: UUID = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb") // GATT 연결하는거 --- LED / PURE

    var uuidSmartRopeDFU_Rookie: UUID = UUID.fromString("0000fe59-0000-1000-8000-00805f9b34fb") // Rookie DFU Service ID
    val uuidSmartRopeDFU_LED:UUID = UUID.fromString("00001530-1212-efde-1523-785feabcd123") // LED DFU Service ID

    var uuidSmartRopeTx: UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e") // 보내는거
    var uuidSmartRopeRx: UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e") // 받는거

    private var mBluetoothGatt: BluetoothGatt? = null

    private var scanning = false
    private val handler = Handler()

    private val SCAN_DURATION: Long = 30000
    private val SCAN_AND_CONNECT_DURATION = (60 * 60 * 1000).toLong()


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
            }
        }, SCAN_AND_CONNECT_DURATION)
    }

    private fun stopScan() {
        if (scanning) {
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.stopScan(scanCallback)
            scanning = false
        }
    }

    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            // do nothing
            Log.e("T", "T")
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            if (results != null) {
                for (i in results.indices) {
                    paringDevice(results[i].device)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            // should never be called
            Log.e("T", "T")
        }
    }


    fun paringDevice(device : BluetoothDevice)
    {
        if(device!= null && device.createBond())
        {
            Hawk.put(Define.Preferences.Device, device)
            stopScan()
            connect(device)
        }
    }

    fun unPairedDevice(): Boolean {

        var saveDevice : BluetoothDevice = Hawk.get(Define.Preferences.Device)

        try {
            saveDevice::class.java.getMethod("removeBond").invoke(saveDevice)
            Hawk.put(Define.Preferences.Device, null)
        } catch (e: java.lang.Exception)  {
        }

        return true
    }


    fun getPairedDevice(): BluetoothDevice? {
        try{
            var saveDevice : BluetoothDevice = Hawk.get(Define.Preferences.Device)
            return saveDevice
        } catch (e: Exception){
            return null
        }
    }

    fun connect(device :BluetoothDevice){
        mBluetoothGatt = device.connectGatt(BaseApplication.context, true, mGattCallback)
    }

    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e("T","T")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e("T","T")

                val serviceList = gatt!!.services
                serviceList?.forEach { gattService ->
                    val gattCharacteristics = gattService.characteristics
                    gattCharacteristics.forEach { gattCharacteristic ->
                        // -- 문자열 읽기쓰기 서비스 바인딩
                        if (gattCharacteristic.uuid == uuidSmartRopeRx || gattCharacteristic.uuid == uuidSmartRopeTx){

                            if( !gatt!!.setCharacteristicNotification(gattCharacteristic,true) ) {
                                Log.e("onServicesDiscovered","setCharacteristicNotification failed")
                                return
                            }
                            //
                            for (des in gattCharacteristic.descriptors) {
                                if (null != des) {
                                    des.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                    gatt!!.writeDescriptor(des)
                                }
                            }

                        }

                    }
                }
            }
        }

        override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e("T","T")
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e("T","T")
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e("T","T")
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e("T","T")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            var test = characteristic.getStringValue(0).toString()
            var count = characteristic.getStringValue(0).toString().toString()
            Log.e("TEST", "[IN]onCharacteristicChanged " + characteristic.uuid)
        }

        override fun onReliableWriteCompleted(gatt: BluetoothGatt, status: Int) {
            Log.e("T", "[IN]onReliableWriteCompleted: " + String.format("Status=0x%02X", status))
        }
    }
}