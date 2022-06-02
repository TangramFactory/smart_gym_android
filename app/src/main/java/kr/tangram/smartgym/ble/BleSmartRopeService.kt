package kr.tangram.smartgym.ble

import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kr.tangram.smartgym.util.log
import java.nio.charset.Charset

class BleSmartRopeService: Service() {

    private var tag = javaClass.name
    private val binder = LocalBinder() // 메니페스트에 붙은 서비스
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    var bluetoothDeviceAddress: String? = null
    private var bluetoothGatt: BluetoothGatt? = null

    private var writeCharacteristic:BluetoothGattCharacteristic?=null

    //
    enum class BleServiceAction {
        CONNECTED,
        DISCONNECTED,
        GATT_SUCCESS,
        GATT_FAIL,
        WRITE_COMPLETE,
        READ_NOTIFY
    }

    //
    override fun onBind(intent: Intent?): IBinder? {
        log(tag,"service binding.. manifest")
        return binder
    }

    //
    private fun broadcastEvent(event: BleServiceAction, data:String?=null) {
        val intent = Intent(event.name)
        if(data!=null) intent.putExtra("data",data)
        sendBroadcast(intent)
    }

    // -- 접속완료 / 서비스를 찾아라
    private val gattCallback = object : BluetoothGattCallback() {

        // 접속했다
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                log(tag,"**ACTION_SERVICE_CONNECTED** $status")
                // 접속
                broadcastEvent(BleServiceAction.CONNECTED)
                gatt.discoverServices()


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                log(tag,"**DISCONNECTED ..... !!!")
                // 끊김
                broadcastEvent(BleServiceAction.DISCONNECTED)

            }
        }

        // 서비스왔다
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log(tag,"**ACTION_SERVICE_DISCOVERED** $status")
                // -> 서비스를 찾아 바인딩
                broadcastEvent(BleServiceAction.GATT_SUCCESS)
            } else {
                log(tag,"**onServicesDiscovered received: $status")
                // GATT 서비스 Fail
                broadcastEvent(BleServiceAction.GATT_FAIL)
            }
        }

        // 처음 접속 했을 때 기기 정보를 받아오는데 쓰인다. -- 사용하지 않음 > 아래 체인지를 사용한다.
        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            //String value = characteristic.getStringValue(0);
            //int value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
            log(tag,"onCharacteristicRead _________ ")
        }

        // 쓰기를 완료때 했을 때 (쓰기 완료가 안오는 경우 다시 같은 데이터를 보낼 수 있도록 버퍼를 적용할 수 있다.)
        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastEvent(BleServiceAction.WRITE_COMPLETE,characteristic.getStringValue(0).toString())
            }
        }

        // 읽기를 완료 했을
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic?) {
            if (characteristic != null) {
                broadcastEvent(BleServiceAction.READ_NOTIFY,characteristic.getStringValue(0).toString())
                log(tag,characteristic.getStringValue(0).toString() )
            }
        }

    }


    //
    fun initialize(): Boolean {
        if (bluetoothManager == null) {
            bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            if (bluetoothManager == null) {
                log(tag,"Unable to initialize BluetoothManager.")
                return false
            }
        }
        bluetoothAdapter = bluetoothManager!!.adapter
        if (bluetoothAdapter == null) {
            log(tag,"Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }

    // A Binder to return to an activity to let it bind to this service
    inner class LocalBinder : Binder(){
        internal fun getService(): BleSmartRopeService {
            return this@BleSmartRopeService
        }
    }

    //
    fun connect(address: String?): Boolean {
        log(tag,"connect start ..")
        try {

            //
            if (bluetoothAdapter == null || address == null) {
                log(tag,"BluetoothAdapter not initialized or unspecified address.")
                return false
            }

            //
            if (bluetoothDeviceAddress != null && address == bluetoothDeviceAddress && bluetoothGatt != null) {
                log(tag,"Trying to use an existing bluetoothGatt for connection.")
                return bluetoothGatt!!.connect()
            }

            //
            val device = bluetoothAdapter!!.getRemoteDevice(address) ?:  return false
            bluetoothGatt = device.connectGatt(this, false, gattCallback)
            bluetoothDeviceAddress = address
            log(tag,"connect finish ..")
            return true

        } catch (e: Exception) {
            log(tag,e.message.toString())
        }

        return false
    }

    // --
    fun disconnect(){
        if(bluetoothAdapter==null||bluetoothGatt==null) return
        bluetoothGatt?.disconnect()
    }

    // Gatt 서비스 목록 가져가기 //
    fun getGattServices():List<BluetoothGattService>? {
        return if (bluetoothGatt == null) { null } else bluetoothGatt!!.services
    }

    // -- 문자열 읽기 서비스 바인딩
    fun setReadCharacteristic(characteristic:BluetoothGattCharacteristic) {
        log(tag,"setReadCharacteristic")
        try {
            if(bluetoothAdapter==null|| bluetoothGatt==null) {
                log(tag,"bluetoothAdapter or bluetoothGatt is null")
                return
            }
            if( !bluetoothGatt!!.setCharacteristicNotification(characteristic,true) ) {
                log(tag,"setCharacteristicNotification failed")
                return
            }
            //
            for (des in characteristic.descriptors) {
                if (null != des) {
                    des.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    bluetoothGatt!!.writeDescriptor(des)
                }
            }
        } catch (e:Exception) { log(tag,e.message.toString()) }
    }

    // -- 문자열 쓰기 서비스 바인딩
    fun setWriteCharacteristic(characteristic:BluetoothGattCharacteristic) {
        log(tag,"setWriteCharacteristic")
        try {
            if (bluetoothAdapter == null || bluetoothGatt == null) return
            writeCharacteristic = characteristic
        } catch (e:Exception) { log(tag,e.message.toString())}
    }
    fun write(data: ByteArray?){
        //
        if (writeCharacteristic == null || data == null || data.size == 0) return
        writeCharacteristic?.value = data
        bluetoothGatt?.writeCharacteristic(writeCharacteristic)
    }
    fun write(data: String?){
        if (data!=null&&data.isNotEmpty()) write(data.toByteArray(Charset.forName("UTF-8")))
    }

    // --

}