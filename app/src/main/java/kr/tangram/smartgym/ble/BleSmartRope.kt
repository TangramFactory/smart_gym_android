package kr.tangram.smartgym.ble

import kr.tangram.smartgym.util.Preferences
import kr.tangram.smartgym.util.log
import org.json.JSONObject
import java.util.*


// --
var uuidRookieGatt: UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e") // GATT 연결하는거 --- 루키꺼
var uuidSmartRopeGatt: UUID = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb") // GATT 연결하는거 --- LED / PURE

var uuidSmartRopeDFU_Rookie: UUID = UUID.fromString("0000fe59-0000-1000-8000-00805f9b34fb") // Rookie DFU Service ID
val uuidSmartRopeDFU_LED:UUID = UUID.fromString("00001530-1212-efde-1523-785feabcd123") // LED DFU Service ID

var uuidSmartRopeTx: UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e") // 보내는거
var uuidSmartRopeRx: UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e") // 받는거

// 접속할 기기 이름에 포함되어 있어야 //
var smartropeNames = arrayListOf<String>("SmartRope","Rookie")

//
val REQUEST_ENABLE_BLUETOOTH = 101

// 20200520
// 생산과정에서 SID가 기록되지 않은 상태로 팔려나간 제품이 있다. 메모리 SID를 HEX로 바꿔봤을 때 모두 아래 SID를 사용하고 있다.
// 절대로 수정하지 말것 !!!
val unknownSIDhex = "efbfbdefbfbdefbfbdefbfbdefbfbdefbfbdefbfbdefbfbdefbfbdefbfbdefbfbdefbfbdefbfbdefbfbdefbfbdefbfbd120203"

//
enum class BleSmartRopeState {
    DISABLE,
    READY,
    SCAN,
    CONNECT,
    CONNECTING,
    JUMPING,
    DISCONNECT,
    DFU_CONNECT
}

//
enum class BleSmartRopePopupEvent {
    OPEN,
    CLOSE
}

//
enum class SmartRopeType {
    ROOKIE,
    LED,
    UNKNOWN
}

// 기기 정보 저장 //
class DeviceInfo {
    private var tag = javaClass.name
    var SID:String?= null
        set(value) {
            field=value
            save()
        }
    var ADDRESS:String? = null
        set(value) {
            field=value
            save()
        }
    var MODEL:String? = null
        set(value) {
            field=value
            save()
        }
    var BATTERY:Int = 0
        set(value) {
            field=value
            save()
        }
    var VERSION:Int = 0
        set(value) {
            field=value
            save()
        }
    var BRIGHTNESS:Int = 0
        set(value) {
            field=value
            save()
        }
    var ALLCOUNT:Int = 0
        set(value) {
            field=value
            save()
        }
    private var infoJSON = JSONObject()

    //
    init {
        log(tag,"smartdevice load ..")
        load()
    }
    //
    fun load() {
        try {
            log(tag, "1")
            log(tag, "load >" + Preferences.get("deviceinfo").toString())
            log(tag, "2")
            set(JSONObject(Preferences.get("deviceinfo").toString()))
            log(tag, "3")
        }catch (e:Exception) {

        }
    }
    //
    fun clear() {
        log(tag,"info reset")
        try {
            Preferences.clear("deviceinfo")
            load() // 데이터를 새거로 바꿔놔야한다 ... null, 0
        }catch (e:Exception) {}
    }
    //
    fun save() {
        infoJSON = JSONObject()
        infoJSON.put("SID",SID)
        infoJSON.put("ADDRESS",ADDRESS)
        infoJSON.put("MODEL",MODEL)
        infoJSON.put("BATTERY",BATTERY)
        infoJSON.put("VERSION",VERSION)
        infoJSON.put("BRIGHTNESS",BRIGHTNESS)
        infoJSON.put("ALLCOUNT",ALLCOUNT)
        //log(tag,"save info : " + infoJSON.toString() )
        try {
            Preferences.set("deviceinfo",infoJSON)
        }catch (e:Exception){ log("deviceinfo",e.message.toString())}
    }
    //
    fun set(json:JSONObject) {
        //log(tag,"set : " + json )
        //
        BATTERY = try {
            json.getInt("BATTERY")
        } catch (e:Exception) { 0 }
        SID = try {
            json.getString("SID")
        } catch (e:Exception) { null }
        MODEL = try {
            json.getString("MODEL")
        } catch (e:Exception) { null }
        ADDRESS = try {
            json.getString("ADDRESS")
        } catch (e:Exception ) { null }
        VERSION = try {
            json.getInt("VERSION")
        } catch (e:Exception) { 0 }
        BRIGHTNESS = try {
            json.getInt("BRIGHTNESS")
        } catch (e:Exception) { 0 }
        ALLCOUNT = try {
            json.getInt("ALLCOUNT")
        } catch (e:Exception) { 0 }
    }
    //
    fun get():JSONObject? {
        return try {
            JSONObject( Preferences.get("deviceinfo").toString() )
        } catch (e:Exception) { null }
    }
}