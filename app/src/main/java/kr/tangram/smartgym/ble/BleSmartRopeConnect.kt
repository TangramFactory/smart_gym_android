package kr.tangram.smartgym.ble

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanSettings
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import com.tangramfactory.smartrope.views.ViewPopHistory
import kr.tangram.smartgym.R
import kr.tangram.smartgym.util.*
import kr.tangram.smartgym.view.ViewDialogPop
import no.nordicsemi.android.dfu.DfuServiceInitiator
import org.json.JSONObject
import java.io.File
import java.util.*


class BleSmartRopeConnect {

    private var tag = javaClass.name
    private var showConnectDialogFlag = false
    private var searchBluetoothConnectFlag = false
    private var connectFlag = false
    //
    lateinit var context: Context // 첫 서비스 구동시 사용되고 -- 엑비비티 이동하면 서비스가 다른데 가서 붙는다 (smartropeInterface)
    private var bluetoothAdapter: BluetoothAdapter? = null
//    private var scanCallback: ScanCallback? = null
    private var bleService: BleSmartRopeService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            bleService = (service as BleSmartRopeService.LocalBinder).getService()
            if (!bleService?.initialize()!!) {
                log(tag, "Unable to initialize")
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bleService = null
        }
    }

    //
    private var connectHandler: Handler = Handler()
    private var connectTimeLast: Long = 0
    private val connectTimeMax: Long = 1000 * 60 * 20 // 20분 동안 점프하지 않으면 끊는다 //
    private var connectRunnable: Runnable? = null
    private var isBind = false

    //
    var AUTOCONNECT = true
    var NAMEFILTER = true
    var INFO = DeviceInfo()
    var STATE = BleSmartRopeState.DISABLE
    var TYPE: SmartRopeType? = null

    // -- 이벤트
    var smartropeInterface: SmartRopeInterface? = null
        set(value) {
            if (field != null) try {
                (field as Context).unregisterReceiver(bleServiceReceiver)
            } catch (e: Exception) {
            }
            field = value
            (field as Context).registerReceiver(bleServiceReceiver, bleServiceIntentFilter())
        }


    fun setInterface(c: Context) {

        if (smartropeInterface == c) return

        // 인터페이스 바꿔치기 // 기본자동 -- 수동가능
        smartropeInterface = c as SmartRopeInterface

        //
        log(tag, " 접속되어 있지 않는 경우 접속을 시도해야지 // setInterface " + STATE + "   auto " + AUTOCONNECT)
        if (!(STATE == BleSmartRopeState.CONNECT || STATE == BleSmartRopeState.CONNECTING || STATE == BleSmartRopeState.DFU_CONNECT) //||STATE==BleSmartRopeState.JUMPING)
                && AUTOCONNECT) {
            log(tag, "START SCAN ... from interface")
            val bondedDevice = bluetoothAdapter?.bondedDevices
            if (!bondedDevice.isNullOrEmpty()){
                log("bonded", bondedDevice?.size.toString())
                return
            }


            scanStart()
        }

    }

    fun unsetInterface(c: Context) {
        try {
            c.unregisterReceiver(bleServiceReceiver)
        } catch (e: Exception) {
        }
    }


    // --
    fun init(c: Context): Boolean {

        log(tag, "init bluetooth")

        context = c

        // 블루투스 어뎁터 생성
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // 블투투스 서비스
        if (bleService == null) {
            log(tag, "bleservice binding !")
            val gattServiceIntent = Intent(context, BleSmartRopeService::class.java)
            isBind = context.bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        // --
        INFO.load()
        log(tag, "load info " + INFO.get())

        // --
        if (bluetoothAdapter!!.isEnabled) {
            setState(BleSmartRopeState.READY)
        } else {
            setState(BleSmartRopeState.DISABLE)
        }

        // --
//        initScanCallback()

        return bluetoothAdapter != null && context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    // -- 블루투스 서비스에서 이벤트 받음
    private val bleServiceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

//            log(tag, "service bleServiceReceiver .. >" + intent.action)

            when (intent.action) {
                // 접속 완료
                BleSmartRopeService.BleServiceAction.CONNECTED.name -> {
                    log(tag, "CONNECTING ..")
                    // 접속 절차 진행
                    setState(BleSmartRopeState.CONNECTING)
                }
                // Gatt 프로파일 확인 완료
                BleSmartRopeService.BleServiceAction.GATT_SUCCESS.name -> {
                    log(tag, "Bluetooth Profile GATT finding .. ")
                    connectGattService()
                }
                BleSmartRopeService.BleServiceAction.WRITE_COMPLETE.name -> {
                }
                // -- 데이터를 받았음
                BleSmartRopeService.BleServiceAction.READ_NOTIFY.name -> {
                    //log(tag,"read " + intent.getStringExtra("data") )
                    var count = intent.getStringExtra("data").toString()
                    commandInterpreter(intent.getStringExtra("data").toString())
                    smartropeInterface?.onRead(intent.getStringExtra("data").toString())
                }
                // --
                BleSmartRopeService.BleServiceAction.DISCONNECTED.name -> {
                    log(tag, "bluetooth disconnected")
                    setState(BleSmartRopeState.DISCONNECT)
                    // 접속이 끊기면 자동으로 찾는다
//                    if (AUTOCONNECT) scanStart()
                }

                BluetoothDevice.ACTION_FOUND ->{
                    val device : BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    connectDevice(device)

                }

            }
        }
    }

    private fun connectDevice(device: BluetoothDevice) {
        if (STATE == BleSmartRopeState.CONNECT || STATE == BleSmartRopeState.JUMPING) {
            // -- 접속되어 있는데 다시 검색하는 경우가 있음 -- 스캔 정지 //
            if (bluetoothAdapter != null && bluetoothAdapter!!.isEnabled) {
                log(tag, "stop scan ~~~")
                //bluetoothAdapter!!.bluetoothLeScanner.flushPendingScanResults(scanCallback)
//                        bluetoothAdapter!!.bluetoothLeScanner.stopScan(scanCallback)
                bluetoothAdapter?.cancelDiscovery()

            }
            return
        }

        // 스마트로프를 찾아요 - 모든 스마트로프 //
        if (checkSmartRope(device)) {
            // 스마트로프 찾았음
//                    log(tag,"........... 이거 스마트로프야 ? " + result?.device?.name + "/ 저장된 값 = " + INFO.SID )
//                    log(tag,"........... " + result?.rssi + "/" + result?.txPower )
            val deviceData = device
            setState(BleSmartRopeState.SCAN)
            // -- 스캔한정보 //
            if (!AUTOCONNECT) {
                // 수동인경우 데이터를 넘긴다
                val jsonDevice = JSONObject()
                jsonDevice.put("name", device.name)
                jsonDevice.put("address", deviceData.address)
                log(tag, " SDK INT : " + Build.VERSION.SDK_INT)

                setState(BleSmartRopeState.SCAN, jsonDevice)

                return
            }

            //기기 검색시 연결가능하게 flag 변경
            if (searchBluetoothConnectFlag) {
                connectFlag = true
                searchBluetoothConnectFlag = false
                return
            }

            //켜지면 아무데나 붙는거 방지! 연결 할껀지 물어보고 연결가능하게 flag 변경
            if (!showConnectDialogFlag) {
                val dialogPop = makeAutoConnectDialogPop()
                (smartropeInterface as Activity)
                    .addContentView(dialogPop,
                        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

                showConnectDialogFlag = true
                return
            }


            if (connectFlag) {
                connect(device)
                connectFlag = false
            }
        }
    }

    private fun bleServiceIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        // 인텐트 필터 자동 추가 //
        BleSmartRopeService.BleServiceAction.values().forEach { value ->
            intentFilter.addAction(value.name)
        }
        return intentFilter
    }


    // -- 기기찾기
    private var scanTime = 0L
    private fun setScanTime() {
        scanTime = System.currentTimeMillis()
    }

    private fun getScanTime(): Long {
        return (System.currentTimeMillis() - scanTime)
    }

    fun scanStart() {

        // --
        log(tag, "scanStart..  $STATE / DFU_MODE = $DFU_MODE")

        if (
                STATE != BleSmartRopeState.DISCONNECT
                && STATE != BleSmartRopeState.READY
                && STATE != BleSmartRopeState.DISABLE
                && STATE == BleSmartRopeState.DFU_CONNECT
        ) return

        if (DFU_MODE) return

        // --
        try { // 블루투스 //
            if (bluetoothAdapter!!.isEnabled) {
                setState(BleSmartRopeState.READY)
            } else {
                setState(BleSmartRopeState.DISABLE)
            }
        } catch (e: Exception) {
            return
        }

        // --
        setScanTime()

        // -- 블루투스가 꺼져 있으면 켜
        if (bluetoothAdapter != null) {
            // -- 블루투스 켜야함
            if (STATE == BleSmartRopeState.DISABLE) {
                val i = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (smartropeInterface != null) (smartropeInterface as Activity).startActivityForResult(i, REQUEST_ENABLE_BLUETOOTH)
                // -->>> 엑티비티에서 이벤트 받아서 켜짐 확인
                log(tag, "bluetooth please interface set .. " + smartropeInterface)
                return
            }
        } else {
            // 블루투스 자체를 사용할 수 없다
            log(tag, "bluetooth is not usable.")
            return
        }

        // --
        log(tag, "start scan ... .")
        ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build() // SCAN MODE //
//        bluetoothAdapter?.bluetoothLeScanner?.startScan(scanCallback)
        bluetoothAdapter?.startDiscovery()
        // --
        setState(BleSmartRopeState.SCAN)
    }

    // --
    fun scanStop() {
        //bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        if (bluetoothAdapter != null && bluetoothAdapter!!.isEnabled) {
            bluetoothAdapter?.cancelDiscovery()
//            bluetoothAdapter!!.bluetoothLeScanner.stopScan(scanCallback)
            setState(BleSmartRopeState.DISCONNECT)
        }
    }

    // --
//    private fun initScanCallback() {
//
//        // 롤리팝이하는 지원하지 않을 거임 !!
//        scanCallback = object : ScanCallback() {
//            @RequiresApi(Build.VERSION_CODES.O)
//            override fun onScanResult(callbackType: Int, result: ScanResult?) {
//                super.onScanResult(callbackType, result)
//                //
//                //if(result?.device?.name!=null ) log(tag, "scan result ${result.device?.name}  / " + STATE )
//                if (STATE == BleSmartRopeState.CONNECT || STATE == BleSmartRopeState.JUMPING) {
//                    // -- 접속되어 있는데 다시 검색하는 경우가 있음 -- 스캔 정지 //
//                    if (bluetoothAdapter != null && bluetoothAdapter!!.isEnabled) {
//                        log(tag, "stop scan ~~~")
//                        //bluetoothAdapter!!.bluetoothLeScanner.flushPendingScanResults(scanCallback)
////                        bluetoothAdapter!!.bluetoothLeScanner.stopScan(scanCallback)
//                        bluetoothAdapter.cancelDiscovery()
//
//                    }
//                    return
//                }
//
//                // 스마트로프를 찾아요 - 모든 스마트로프 //
//                if (checkSmartRope(result)) {
//                    // 스마트로프 찾았음
////                    log(tag,"........... 이거 스마트로프야 ? " + result?.device?.name + "/ 저장된 값 = " + INFO.SID )
////                    log(tag,"........... " + result?.rssi + "/" + result?.txPower )
//                    val deviceData = result?.device!!
//                    setState(BleSmartRopeState.SCAN)
//                    // -- 스캔한정보 //
//                    if (!AUTOCONNECT) {
//                        // 수동인경우 데이터를 넘긴다
//                        val jsonDevice = JSONObject()
//                        jsonDevice.put("name", deviceData.name)
//                        jsonDevice.put("address", deviceData.address)
//                        jsonDevice.put("rssi", result?.rssi)
//                        log(tag, " SDK INT : " + Build.VERSION.SDK_INT)
//                        if (Build.VERSION.SDK_INT >= 26) {
//                            try { // 일부기기 지원안함 (갤럭시S4)
//                                jsonDevice.put("txPower", result?.txPower)
//                            } catch (e: Exception) {
//                            }
//                            try { // 일부기기 지원안함
//                                jsonDevice.put("isConnectable", result?.isConnectable)
//                            } catch (e: Exception) {
//                            }
//                        }
//                        setState(BleSmartRopeState.SCAN, jsonDevice)
//
//                        return
//                    }
//
//                    //기기 검색시 연결가능하게 flag 변경
//                    if (searchBluetoothConnectFlag) {
//                        connectFlag = true
//                        searchBluetoothConnectFlag = false
//                        return
//                    }
//
//                    //켜지면 아무데나 붙는거 방지! 연결 할껀지 물어보고 연결가능하게 flag 변경
//                    if (!showConnectDialogFlag) {
//                        val dialogPop = makeAutoConnectDialogPop()
//                        (smartropeInterface as Activity)
//                            .addContentView(dialogPop,
//                                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
//
//                        showConnectDialogFlag = true
//                        return
//                    }
//
//
//                    if (connectFlag) {
//                        context.startActivity(Intent(context,DeviceDiscoverActivity::class.java))
//                        connectFlag = false
//                    }
//
//                }
//            }
//        }
//    }

    private fun makeAutoConnectDialogPop(): ViewDialogPop {
        val dialogPop = ViewDialogPop(context)
        dialogPop.titleDisable()
        dialogPop.HtmlDescription = "연결가능 기기를 검색하였습니다 연결하시겟습니까?"
        dialogPop.left = "no"
        dialogPop.right = "yes"
        dialogPop.buttonRight.setBackgroundResource(R.drawable.button_background_green)
        dialogPop.setTouchBackgroundCloseLeftAction()
        dialogPop.backgroundBlurDisable() // 배경에 블러를 사용하면 windowpop이 사라진다
        dialogPop.rightAction = {
            connectFlag = true
        }

        return dialogPop
    }

    // -- 스마트로프 확인
    private fun checkSmartRope(device: BluetoothDevice?): Boolean {
        //
//        if (device.uuids == null || device == null || device?.name == null) return false
        //uuid 안받아와짐...
        if (device == null || device?.name == null) return false
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) if (!result.isConnectable) return false

        // SID가 잘 못 된 경우 접속해서 SID를 지정하고 리부팅을 진행한다.
        fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
//        if (result.scanRecord!!.serviceUuids.size == 1 && result.device.name.toByteArray().toHexString() == unknownSIDhex) {
        if (device?.name.toByteArray().toHexString() == unknownSIDhex) {
            log(tag, "KNOWN... 검색됨........................................")
            TYPE = SmartRopeType.UNKNOWN
            return true
        }

        // 이름에 스마트로프 이름이 있는지 확인 // SID 와 서비스UUID 2가지를 확인 //
        smartropeNames.forEach { str ->

//            log(tag," 저장됨 INFO   .... " + INFO.SID + " / " + INFO.ADDRESS  )
//            log(tag," 검색한 DEVICE .... " + result.device.name + "("+ result.device.name.length +") "+  "/ " + result.device.address )
//            log(tag," UUID   .... " + result.scanRecord!!.serviceUuids[0].toString().trim() )//+ "=" + uuidSmartRopeDFU_LED.toString().trim() + "/" + uuidSmartRopeDFU_Rookie.toString().trim() )
//            uuidSmartRopeDFU_LED

            // -- 저장된 정보가 있거나 없거나 DFU 모드인 기기가 있으면 무조건 붙는다.
            if (device.name.contains(str)) {
                // Rookie
                log(tag, "DFU Connection ... . ROOKIE")
                TYPE = SmartRopeType.ROOKIE
                DFU_MODE = true
                return true
            } else if (device.name == "UPDATE") {
                // LED
                log(tag, "DFU Connection ... . LED ")
                TYPE = SmartRopeType.LED
                DFU_MODE = true
                return true
            }
//            else

//                if (INFO.ADDRESS != null
//                        && INFO.SID != null
//                        && AUTOCONNECT) {

                    // 저장된 정보가 있으면 붙는다.

                    TYPE = SmartRopeType.ROOKIE
                    uuidSmartRopeGatt
//                    if (device.name == INFO.SID && device.address == INFO.ADDRESS) {
//                        if (result.scanRecord!!.serviceUuids[0].toString().trim() == uuidRookieGatt.toString().trim()) {
//                            TYPE = SmartRopeType.ROOKIE
//                        } else if (result.scanRecord!!.serviceUuids[0].toString().trim() == uuidSmartRopeGatt.toString().trim()) {
//                            TYPE = SmartRopeType.LED
//                        }
//                        return true
//                    }

//                } else {
//
////                log(tag,"SCANED .............")
//                    // 저장된 정보가 없으면 무조건 붙는다.
//                    if (device.name.contains(str) && result.scanRecord!!.serviceUuids[0].toString().trim() == uuidRookieGatt.toString().trim()) {
//                        // 루키다 !
//                        TYPE = SmartRopeType.ROOKIE
//                        return true
//                    } else
//                        if (result.device.name.contains(str) && result.scanRecord!!.serviceUuids[0].toString().trim() == uuidSmartRopeGatt.toString().trim()) {
//                            // 엘이뒤다 !
//                            TYPE = SmartRopeType.LED
//                            return true
//                        } else if (!NAMEFILTER && result.scanRecord!!.serviceUuids[0].toString().trim() == uuidRookieGatt.toString().trim()) {
//                            //
//                            TYPE = SmartRopeType.UNKNOWN
//                            return true
//                        }
//
//                }

        }

        // 찾을 수 없음
        return false
    }

    // -- 접속 시작
    private fun connect(device: BluetoothDevice) {
        log(tag, "connect " + device.address)

        // -- 확실히 정지해야하는데 ... .
        // 스마트로프 찾았으니까 스캔정지
        scanStop()

        //
        if (bleService != null) {
            log(tag, "bleService connect ..")
            if (!DFU_MODE) {
                INFO.SID = device.name
                INFO.ADDRESS = device.address
            }
            if (!bleService!!.connect(device.address)) log(tag, "--------- ERROR ..") // **서비스 바인딩 ** //
        } else {
            //
            log(tag, "bleService is null !")
        }
    }

    fun disconnect() {
        log(tag, "disconnected . . .")
        try {
            bleService!!.disconnect()
            connectHandler.removeCallbacks(connectRunnable!!)
        } catch (e: Exception) {
        }
        STATE = BleSmartRopeState.DISCONNECT
        TYPE = null
    }

    // =============================================================================================
    // -- 접속 절차 진행 ---
    private fun connectGattService() {

        // -- 읽기 / 쓰기 서비스 찾아서 이벤트 걸기
        val serviceList = bleService?.getGattServices()
        serviceList?.forEach { gattService ->

            log(tag, " uuid " + gattService.uuid)

            // -- Normal Connect
            if (gattService.uuid == uuidSmartRopeGatt || gattService.uuid == uuidRookieGatt) {
                val gattCharacteristics = gattService.characteristics
                log(tag, "  connect uuid " + gattService.uuid)
                gattCharacteristics.forEach { gattCharacteristic ->
                    log(tag, "      inner uuid " + gattCharacteristic.uuid)
                    // -- 문자열 읽기쓰기 서비스 바인딩
                    if (gattCharacteristic.uuid == uuidSmartRopeRx) bleService?.setReadCharacteristic(gattCharacteristic)
                    if (gattCharacteristic.uuid == uuidSmartRopeTx) bleService?.setWriteCharacteristic(gattCharacteristic)
                }
            }

            // -- DFU Connect
            if (gattService.uuid == uuidSmartRopeDFU_Rookie || gattService.uuid == uuidSmartRopeDFU_LED) {
                // 알아서 하니까 기타 등등 불필요.
                setState(BleSmartRopeState.DFU_CONNECT)
                dfuQuestion()
                return
            }

        }

        //-------------------------------------------------------------------
        // 연결되었어 -
        // -- GATT RX / TX 서비스 바인딩이 끝나면 Hello ?
        if (!DFU_MODE) {

            Handler().postDelayed({
                log(tag, "HELLO? -------------- ")
                write("HELLO?")
                //
                Handler().postDelayed({
                    // 인트로를 다본 상태에서만 .. 히스토리를 봐라 //
                    if (Preferences.getBoolean("intro")) {
                        when (TYPE) {
                            SmartRopeType.ROOKIE -> {
                                //log(tag,"ROOKIE connected..")
                                write("HTC?")
                            }
                            SmartRopeType.LED -> {
                                //log(tag,"LED connected..")
                                write("HCOUNT?")
                            }
                            SmartRopeType.UNKNOWN -> {
                                // --- SID 잘 못된 경우
                                write("SIR!")
                                Handler().postDelayed({
                                    write("RST!")
                                    INFO.set(JSONObject())
                                    disconnect()
                                }, 300) // 500
                            }
                        }
                    }
                }, 300)  // 500
            }, 300) // 500

            // --- 연결된 상태 체크 //
            connectRunnable = Runnable {

                // 앱 종료
                autoClose()

                // LED 인경 우 연결된 상태에서 계속 보낸다
                if (TYPE == SmartRopeType.LED) write("-")
                if (STATE == BleSmartRopeState.CONNECT) connectHandler.postDelayed(connectRunnable!!, 1000)
            }
            connectTimeLast = Date().time
            connectHandler.postDelayed(connectRunnable!!, 600)


            setState(BleSmartRopeState.CONNECT)

        } else {
            setState(BleSmartRopeState.DFU_CONNECT)
        }
    }

    // ---
    private fun autoClose() {
        // 시스템 설정에서 설정된 경우에만 ..
        if (AppSetting.ble_powersave > 0) {

            //
            val timeGap = Date().time - connectTimeLast
            val isForeGround = try {
                CheckForeGround().execute(context).get()
            } catch (e: Exception) {
                false
            }
            //
            if (isForeGround!!) {
                connectTimeLast = Date().time
            } else {
                // DFU모드가 아니고 연결된 상태에서 백그라운드에 있으면 //
                if (!DFU_MODE && timeGap > connectTimeMax) {
                    BleNotificator().closeAction(context)
                }
            }

        }
    }

    //
    fun write(str: String) {
        bleService?.write(str.trim())
    }

    // --------------
    private fun setState(s: BleSmartRopeState, j: JSONObject? = null) {
        STATE = s
        if (j != null) {
            if (smartropeInterface != null) smartropeInterface?.onState(STATE, j)
        } else {
            if (smartropeInterface != null) smartropeInterface?.onState(STATE, null)
        }

        //
        // 접속이 끊어진경우 -- 창닫기
        if (STATE == BleSmartRopeState.DISCONNECT) {
            log(tag, "끊어졌어 !!!")
            closeJumpCounterView()
        }
    }

    // -- INTERFACE
    interface SmartRopeInterface {
        fun isEnable(): Boolean = true
        fun onState(state: BleSmartRopeState, message: JSONObject? = null)
        fun onCount(event: BleSmartRopePopupEvent)
        fun onRead(data: String)
    }

    //**********************************************************************************************
    private fun dfuQuestion() {

        // 펌웨어 업데이트를 종용하는 팝업을 띄움 //
        val targetActivity = smartropeInterface as Activity
        val popupFirmware = targetActivity.layoutInflater.inflate(R.layout.layout_pop_firmware, null)
        val popupWindow = PopupWindow(popupFirmware, targetActivity.resources.displayMetrics.widthPixels, targetActivity.resources.displayMetrics.heightPixels, true)
        popupWindow.animationStyle = android.R.style.Animation_Dialog
        popupWindow.showAtLocation(popupFirmware, Gravity.CENTER, 0, 0)
        dimBehind(popupWindow)

        val popupBtnLeft = popupFirmware.findViewById<Button>(R.id.button_left)
        val popupBtnRight = popupFirmware.findViewById<Button>(R.id.button_right)

        // -- 루키 / LED
        val waitHandler = Handler()
        var waitRunnable: Runnable? = null
        waitRunnable = Runnable {
            kotlin.run {
                log(tag, "waiting .. " + STATE)
                if (STATE == BleSmartRopeState.CONNECTING && !DFU_MODE) {
                    waitHandler.postDelayed(waitRunnable!!, 1000)
                } else {
                    Handler().postDelayed({
                        popupWindow.dismiss()
                        disconnect()
                    }, 2000)
                }
            }
        }
        waitHandler.postDelayed(waitRunnable, 1000)

        // --
        popupBtnLeft.setOnClickListener {
            popupWindow.dismiss()
            Handler().postDelayed({
                disconnect()
            }, 2000)
        }

        popupBtnRight.setOnClickListener {
            popupWindow.dismiss()
//            val intent = Intent(targetActivity, DeviceFirmwareActivity::class.java)
//            targetActivity.startActivity(intent)
        }

    }

    fun dfuUpload() {

        //
        val firmwarePath = File("/data/data/" + context.packageName + "/firmware")
        val device = bluetoothAdapter!!.getRemoteDevice(bleService?.bluetoothDeviceAddress)

        log(tag, " ----------- ")
        log(tag, " device " + device + "/" + device.address + "/" + device.name)

        val starter = DfuServiceInitiator(device.address).setDeviceName(device.name).setKeepBond(false)
        val firmwareFile = firmwarePath.path + "/firmware.zip"
        log(tag, "file " + firmwareFile)

        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true)
        starter.setPrepareDataObjectDelay(300L)

        starter.setZip(firmwareFile)
        //starter.start(context, DfuService::class.java)
        starter.start((smartropeInterface as Context), DfuService::class.java)
        //---
//        setState(BleSmartRopeState.DFU_CONNECT)
        //

    }

    //**********************************************************************************************
    var privateCount: Int = 0
    var publicCount: Int = 0
    private var bufferString: String = ""
    fun commandInterpreter(str: String) {

        // 스트링 버퍼
        bufferString += str

        if (str.contains(";")) {
            //log(tag, "R " + bufferString )
            // 데이터 처리
            bufferString = bufferString.replace(";", "")
            val data: List<String> = bufferString.split(":")
            bufferString = ""

            when (data[0]) {
                // 베터리
                "BAT" -> {
                    var batteryPercent = try {
                        data[1].toInt()
                    } catch (e: Exception) {
                        0
                    }
                    if (batteryPercent > 100) batteryPercent = 100
                    if (batteryPercent < 0) batteryPercent = 0
                    INFO.BATTERY = batteryPercent
                }
                //
                "MDL" -> {
                    INFO.MODEL = try {
                        data[1].trim()
                    } catch (e: Exception) {
                        ""
                    }
                }
                // SID 지정
                "SID" -> {
                    INFO.SID = try {
                        data[1].trim()
                    } catch (e: Exception) {
                        ""
                    }
                }
                // 펌웨어 버전
                "VER" -> {
                    INFO.VERSION = try {
                        data[1].toInt()
                    } catch (e: Exception) {
                        0
                    }
                }
                // LED밝기
                "LBG" -> {
                    INFO.BRIGHTNESS = try {
                        data[1].toInt()
                    } catch (e: Exception) {
                        0
                    }
                }
                // 최종 카운트
                "ACT" -> {
                    INFO.ALLCOUNT = try {
                        data[1].toInt()
                    } catch (e: Exception) {
                        0
                    }
                }
                // 점프
                "CRI", "CNT" -> { // Rookie & LED
                    //log(tag,"PRIVATE COUNT : " + privateCount )
                    connectTimeLast = Date().time
                    val jump = try {
                        data[1].toInt()
                    } catch (e: Exception) {
                        0
                    }
                    if (jump == publicCount) return // 이전 카운트 값과 같을 경우 카운트하지 않는다. )데이터가 중복해서 온것임)
                    privateCount++
                    publicCount = jump
                    if (smartropeInterface != null) {
                        smartropeInterface?.onState(BleSmartRopeState.JUMPING)
                        openJumpCounterView()
                    }
                }
                // 더블 탭
                "DCLK" -> {
                    closeJumpCounterView()
                }
                // 히스토리
                "HCOUNT" -> {
                    // 이전에 운동했던 히스토리가 있다 !
                    val totalHistory = try {
                        data[1].toInt()
                    } catch (e: Exception) {
                        0
                    }
                    if (totalHistory > 0) {
                        getHistoryData(totalHistory)
                    } // 히스토리 받아 - Background
                }
                // 루키 히스토리
                "HTC" -> {
                    // 루키에서 이전에 운동했던 히스토리가 있다 !
                    val totalHistory = try {
                        data[1].toInt()
                    } catch (e: Exception) {
                        0
                    }
                    //log(tag , "루키 HTC totalHistory : " + totalHistory)
                    if (totalHistory > 0) {
                        getHistoryData(totalHistory)
                    } // 히스토리 받아 - Background
                }
                //
                "H" -> {
                    // Index : Count : Calorie : Duration : StartedAt : EndedAt : Device Time
                    setHistoryData(data)
                }

            }

        }
    }

    //==== 팝업 카운터를 보여준다
    private var viewHandler: Handler? = null
    private var viewParentContext: Context? = null
    var enableCounter = true
    fun openJumpCounterView() {

        if (smartropeInterface == null) return
        if (!enableCounter) return // 카운터가

        //
        viewHandler = Handler(Looper.getMainLooper())
        viewHandler!!.post {

            //
        }
    }

    // 접속이 끊어졌을 때 팝업 카운터를 닫는다 !
    fun closeJumpCounterView() {
        Handler(Looper.getMainLooper()).post { // On Main Looper

        }
    }

    //
    fun popOpened(): Boolean {
        return false
    }

    //==== 히스토리뷰를 보여준다
    private var viewHistory: ViewPopHistory? = null
    private fun getHistoryData(totalHistory: Int) {
        //
        openHistoryView()
        // 히스토리 데이터 가져오기 // 이래서 느린거구나 .. 왜그랬니 ㅠㅠ
        var i = 1.0f
        val phase = 100L
        var percent: Float
        val historyHandler = Handler(Looper.getMainLooper())
        val historyRunnable = object : Runnable {
            override fun run() {
//                if (ropeType.equals("S")){
                if (TYPE == SmartRopeType.LED) {
                    write("HISTORY?")
                    percent = i / totalHistory
                    log(tag, "Type S : " + (i - 1.0f))
                    viewHistory?.setProgress(percent)
                    if (percent == 1.0f) viewHistory?.onDataReceiveComplete()
                    //
                    if (i < totalHistory) {
                        historyHandler.postDelayed(this, phase)
                        i++
                    }
                } else {
                    write("HTD:" + (i - 1.0f))
                    percent = i / totalHistory
                    log(tag, "Type R : " + (i - 1.0f))
                    viewHistory?.setProgress(percent)
                    if (percent == 1.0f) viewHistory?.onDataReceiveComplete()
                    //
                    if (i < totalHistory) {
                        historyHandler.postDelayed(this, phase)
                        i++
                    }
                }
            }
        }
        //
        if (TYPE == SmartRopeType.LED) write("HINDEX:0")
        historyHandler.postDelayed(historyRunnable, phase)
    }

    private fun setHistoryData(data: List<String>) {
        if (viewHistory == null) return
        viewHistory?.addHistoryData(data)
        log(tag, "Type R : data : " + (data))
    }

    private fun openHistoryView() {
        if (smartropeInterface == null) return
        //
        viewHandler = Handler(Looper.getMainLooper())
        viewHandler!!.post {
            if (viewParentContext != (smartropeInterface as Context)) viewHistory = null // 부모가 바뀌었으면 NULL
            viewParentContext = (smartropeInterface as Context)
            viewHistory = ViewPopHistory(viewParentContext!!)
        }
    }

    fun setFlag(b: Boolean) {searchBluetoothConnectFlag = b}


}

