package kr.tangram.smartgym.ui.deviceConnect

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import kr.tangram.smartgym.R
import kr.tangram.smartgym.ble.BleSmartRopeConnect
import kr.tangram.smartgym.ble.BleSmartRopePopupEvent
import kr.tangram.smartgym.ble.BleSmartRopeState
import kr.tangram.smartgym.databinding.ActivityDeviceDiscoverBinding
import kr.tangram.smartgym.databinding.LayoutDiscoverDeviceinfoBinding
import kr.tangram.smartgym.util.bleConnection
import kr.tangram.smartgym.util.log
import kr.tangram.smartgym.util.toPx

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.json.JSONArray
import org.json.JSONObject

/**
 * 새로운 기기를 검색한다
 */

//
class DeviceDiscoverActivity : AppCompatActivity() , BleSmartRopeConnect.SmartRopeInterface{

    private val tag:String = "DeviceDiscoverActivity"
    private lateinit var bind : ActivityDeviceDiscoverBinding
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityDeviceDiscoverBinding.inflate(LayoutInflater.from(this))
        setContentView(bind.root)

        //
        OverScrollDecoratorHelper.setUpOverScroll(bind.scrollView)

        //
        bind.buttonBack.setOnClickListener{
            onBackPressed()
        }

    }

    //
    override fun onStart() {
        super.onStart()
        // 끊고 ! 스탑하고 !
        bleConnection.AUTOCONNECT = false
        bleConnection.scanStart()
    }

    //
    private var count = 0
    private var deviceArray = JSONArray()
    //private var deviceList:ArrayList<BluetoothDevice> = ArrayList()
    fun addDevice(jsonDevice:JSONObject) {


        log(tag,"device info: " + jsonDevice + " / " + deviceArray.length() )

        // 이미 있는 경우 >> 업데이트 >> 뷰도 업데이트 해주자 !
        if(deviceArray.length()>0) for ( i in 0 until deviceArray.length()) {
            val deviceInfo = deviceArray[i] as JSONObject
            // 같은 이름이 있으면 .. 업데이트한다.
            if(deviceInfo.getString("address")==jsonDevice.getString("address")) {
                deviceArray.put(i,jsonDevice)
                // -- 그 뷰도 찾아서 업데이트 한다
                return
            }
        }

        // 뷰를 붙여 넣어용 ~
        val viewDeviceInfo = this.layoutInflater.inflate(R.layout.layout_discover_deviceinfo,null)
        val bind = LayoutDiscoverDeviceinfoBinding.inflate(LayoutInflater.from(this))
        bind.root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, toPx(this,100f ) ) // 리스트 높이 //
        bind.root.tag = count
        bind.textTag.text = jsonDevice.getString("name")
        bind.textSid.text =  "RSSI:" + jsonDevice.getString("rssi") + "db, ADDR:" + jsonDevice.get("address")
        this.bind.layoutList.addView(viewDeviceInfo)
        this.bind.layoutList.invalidate()

        // 뷰를 누르면 이벤트를 발생
        bind.buttonConnect.setOnClickListener { view ->
            val id = viewDeviceInfo.tag as Int
            val json = deviceArray.get(id) as JSONObject

            log(tag,"SELECT : " + json )

            // 데이터를 저장 >>> 다음 부터 이놈에게 계속 접속
            bleConnection.INFO.set(JSONObject())
            bleConnection.INFO.SID = try { json.getString("name") } catch (e:Exception) {""}
            bleConnection.INFO.ADDRESS = try { json.getString("address") } catch (e:Exception) {""}

            onBackPressed()
        }

        //
        deviceArray.put(count,jsonDevice)
        count ++


//    fun addDevice(device:BluetoothDevice?) {
//        if(device==null)return
//
//        log(tag,"ADD VIEW !!" + device.name + " : " + device.bluetoothClass + " : " + device.bondState + " : " + device.type + " : ")
//
//        // 이미 등록했으면 나가 ~
//        for(d in deviceList) if(d==device) return
//
//        // 뷰를 붙여 넣어용 ~
//        val viewDeviceInfo = this.layoutInflater.inflate(R.layout.layout_discover_deviceinfo,null)
//        viewDeviceInfo.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, toPx(this,100) ) // 리스트 높이 //
//
//        viewDeviceInfo.tag = count
//        deviceList.add(count, device)
//
//        layout_list.addView(viewDeviceInfo)
//        count ++
//
//        //
//        viewDeviceInfo.text_tag.text = device.name
//        viewDeviceInfo.text_address.text = "RSSI:" + SRDecive.foundRssi + "db, ADDR:" + device.address // 신호 강도
//
//        // 뷰를 누르면 이벤트를 발생
//        viewDeviceInfo.button_connect.setOnClickListener { view ->
//
//            val id = viewDeviceInfo.tag as Int
//            val device = deviceList.get(id)
//
//            log(tag,"-----------------------press " + "/ " + device )
//
//            // 데이터를 저장 >>> 다음 부터 이놈에게 계속 접속
//            val deviceInfo:SmartRopeDevice.DeviceInfo? = SmartRopeDevice.DeviceInfo()
//            deviceInfo?.ADDRESS = device.address
//            deviceInfo?.SID = device.name
//            Preferences.set("deviceinfo", GsonBuilder().create().toJson(deviceInfo) )
//
//            onBackPressed()
//        }
    }

    override fun onState(state: BleSmartRopeState, message: JSONObject?) {

        log(tag,".... " + state  )
        if(message!=null) log(tag,"... " + message )
        if(state==BleSmartRopeState.SCAN&&message!=null) addDevice(message)

//        //
//        when(state) {
//            BleSmartRopeState.SCAN->{
//                if(message!=null) {
//                }
//            }
////            SmartRopeDevice.DeviceStatus.IS_FOUND -> {
////                log(tag, "FOUND ............." + SRDecive.foundDevice?.name )
////                runOnUiThread {
////                    addDevice(SRDecive.foundDevice!!)
////                }
////            }
////            SmartRopeDevice.DeviceStatus.IS_DISCOVERING -> {
////                button_retry.alpha = 0.5f
////                button_retry.text = getString(R.string.setting_discover_doing)
////            }
////            else -> {
////                button_retry.alpha = 1.0f
////                button_retry.text = getString(R.string.setting_discover_start)
////            }
//        }
    }

    override fun onCount(event: BleSmartRopePopupEvent) {
        TODO("Not yet implemented")
    }

    override fun onRead(data: String) {
        TODO("Not yet implemented")
    }


    //
    override fun onBackPressed() {
        log(tag,"ON BACK !!!")

        if(bleConnection.popOpened()) {
            // 팝업이 떠있으면 팝업 닫고
            bleConnection.closeJumpCounterView()
        } else {
            super.onBackPressed()
            //
            bleConnection.scanStop()
            bleConnection.unsetInterface(this)
            //bleConnection.AUTOCONNECT = true
            //
            Handler().postDelayed({
                finish()
                overridePendingTransition(R.anim.in_left, R.anim.out_right)
            },500)
        }

    }


}
