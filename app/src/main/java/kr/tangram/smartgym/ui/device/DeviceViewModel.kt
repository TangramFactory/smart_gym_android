package kr.tangram.smartgym.ui.device

import android.util.Log
import kr.tangram.smartgym.data.repository.DeviceRegisterRepository
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hwangjr.rxbus.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.base.BaseViewModel
import kr.tangram.smartgym.ble.SmartRopeManager
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.data.domain.model.JumpData
import kr.tangram.smartgym.data.remote.model.DeviceInfo
import kr.tangram.smartgym.data.remote.request.JumpSaveObject
import kr.tangram.smartgym.data.remote.request.ReqDeviceLoad
import kr.tangram.smartgym.data.remote.response.BaseResponse
import kr.tangram.smartgym.data.remote.response.DeviceListResponse
import kr.tangram.smartgym.data.repository.WorkOutRepository
import kr.tangram.smartgym.util.Define
import kr.tangram.smartgym.util.getNowDateFormat
import no.nordicsemi.android.support.v18.scanner.ScanResult
import org.koin.core.component.inject

class DeviceViewModel(
): BaseViewModel() {

    private val deviceRegisterRepository: DeviceRegisterRepository by inject()
    private val workoutRepository: WorkOutRepository by inject()

    private val _myDeviceList = MutableLiveData<List<DeviceRegister>>()
    var myDeviceList : LiveData<List<DeviceRegister>> = _myDeviceList

    private val _scanDevice = MutableLiveData<ScanResult>()
    var scanDevice : LiveData<ScanResult> = _scanDevice

    private val _stopScan = MutableLiveData<Unit>()
    var stopScan : LiveData<Unit> = _stopScan


    private val _deviceRegister = MutableLiveData<DeviceRegister>()
    var deviceRegister : LiveData<DeviceRegister> = _deviceRegister

    private val _deleteDevice = MutableLiveData<Unit>()
    var deleteDevice : LiveData<Unit> = _deleteDevice

    private val _updateDeviceAlias = MutableLiveData<Unit>()
    var updateDeviceAlias : LiveData<Unit> = _updateDeviceAlias

    private val _deviceLoadList = MutableLiveData<List<DeviceInfo>>()
    var deviceLoadList : LiveData<List<DeviceInfo>> = _deviceLoadList

    init {

        smartRopeManager = SmartRopeManager.getInstance().apply {

            onFound =  { scanResult ->
                _scanDevice.postValue(scanResult)
            }
            onStopScan = {
                _stopScan.value = Unit
            }
        }
    }


    fun saveDevice(scanResult: ScanResult) {

        deviceRegisterRepository.getDeviceList(scanResult.device.address).doOnSuccess {

            // 중복 여부 체크
            if(it.isNullOrEmpty()){

                var type = if(scanResult.scanRecord?.deviceName!!.contains(Define.DeviceInfo.Type.Rookie)) Define.DeviceInfo.Type.Rookie
                else Define.DeviceInfo.Type.Led

                var deviceRegister = DeviceRegister(scanResult.scanRecord?.deviceName, true, 100, false, "",
                    getNowDateFormat("yyyyMMddHHmmss"), scanResult.device.address,
                    scanResult.scanRecord?.deviceName, "",
                    type)

                deviceRegisterRepository.insertDevice(deviceRegister)
                RxBus.get().post(Define.BusEvent.DeviceState, "");

                showMessage("등록 완료")
            } else {
                showMessage("이미 저장됨")
            }
        }.subscribe()

    }

    fun startScan()
    {
        smartRopeManager?.startScan()
    }

    fun stopScan()
    {
        smartRopeManager?.stopScan()
    }


    fun getDeviceRegisterList()
    {
        getCompositeDisposable().add(
            deviceRegisterRepository.getDeviceList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<List<DeviceRegister>>() {
                    override fun onNext(response: List<DeviceRegister>) {
                        _myDeviceList.postValue(response)
                        getCompositeDisposable().clear()
                    }

                    override fun onError(e: Throwable) {
                    }
                    override fun onComplete() {}
                })
        )
    }

    fun getDeviceRegister(identifier: String)
    {
        deviceRegisterRepository.getDeviceList(identifier).doOnSuccess {
            if(!it.isNullOrEmpty()){
                _deviceRegister.postValue(it[0])
            }
        }.subscribe()
    }

    fun updateDeviceRegister(identifier: String, auto: Boolean)
    {
        deviceRegisterRepository.updateDeviceAuto(auto, identifier)
        RxBus.get().post(Define.BusEvent.DeviceState, "");
    }

    fun updateDeviceName(name: String, identifier: String)
    {
        deviceRegisterRepository.updateDeviceName(name, identifier)
    }


    fun disConnect(identifier: String)
    {
        smartRopeManager?.disconnect(identifier)
    }

    fun deleteDeviceRegister(identifier: String)
    {
        deviceRegisterRepository.deleteDevice(identifier)
        RxBus.get().post(Define.BusEvent.DeviceState, "");
        _deleteDevice.value = Unit
    }

    fun addRelease(identifier: String)
    {
        smartRopeManager?.addRelease(identifier)
    }

    fun removeRelease(identifier: String)
    {
        smartRopeManager?.removeRelease(identifier)
    }

    fun clearRelease()
    {
        smartRopeManager?.clearRelease()
    }

    fun updateDeviceAlas(deviceRegister: DeviceRegister, alias: String)
    {
        var deviceInfo = DeviceInfo(Define.HardCording.Uid, "A",
            deviceRegister.device_sid!!, deviceRegister.identifier!!, alias)

        addDisposable(
            deviceRegisterRepository.updateDeviceAlas(deviceInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<BaseResponse>() {
                    override fun onNext(response: BaseResponse) {

                        if(response.result?.resultCode == Define.ResCode.SUCCESS) {

                            updateDeviceName(alias, deviceRegister.identifier)

                            _updateDeviceAlias.value = Unit

                            RxBus.get().post(Define.BusEvent.DeviceState, "");
                        } else{
                            showMessage("update fail")
                        }
                    }

                    override fun onError(e: Throwable) {}
                    override fun onComplete() {}
                })
        )
    }


    fun getDeviceLoadList(list : List<DeviceRegister>)
    {
        var deviceList = ArrayList<DeviceInfo>()

        for(i in 0 until list.size)
        {
            deviceList.add(DeviceInfo("", "", list[i].device_sid!!, list[i].identifier!!, ""))
        }

        addDisposable(
            deviceRegisterRepository.getDeviceLoadList(ReqDeviceLoad("A", deviceList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<DeviceListResponse>() {
                    override fun onNext(response: DeviceListResponse) {

                        if(response.result?.resultCode == Define.ResCode.SUCCESS && !response.resultList.isNullOrEmpty()) {
                            _deviceLoadList.postValue(response.resultList)
                            getCompositeDisposable().clear()
                        }
                    }

                    override fun onError(e: Throwable) {}
                    override fun onComplete() {}
                })
        )
    }


    fun getLocalJumpData()
    {
        getCompositeDisposable().add(
            workoutRepository.getJumpDataList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<List<JumpData>>() {
                    override fun onNext(response: List<JumpData>) {

                        Log.e("getLocalJumpData", response.size.toString())
                        updateJumpData(response)
                    }

                    override fun onError(e: Throwable) {
                        Log.e("T","T")
                    }
                    override fun onComplete() {
                        Log.e("T","T")
                    }
                })
        )
    }


    fun updateJumpData(list: List<JumpData>)
    {
        var saveDataList = mutableListOf<JumpSaveObject.JumpSaveData>()

        for(i in 0 until list.size)
        {
            var saveData = JumpSaveObject.JumpSaveData("", "", list[i].wid!!, list[i].did!!, list[i].mid!!,
                list[i].jump!!, list[i].avg!!, list[i].calorie!!, list[i].duration!!, list[i].finish!!, true)

            saveDataList.add(saveData)
        }


        getCompositeDisposable().add(
            workoutRepository.saveJumpWorkOutHistory(JumpSaveObject(Define.HardCording.Uid, saveDataList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<BaseResponse>() {
                    override fun onNext(response: BaseResponse) {

                        Log.e("T", "T")
                    }

                    override fun onError(e: Throwable) {
                        Log.e("T","T")
                    }
                    override fun onComplete() {
                        Log.e("T","T")
                    }
                })
        )

    }

}