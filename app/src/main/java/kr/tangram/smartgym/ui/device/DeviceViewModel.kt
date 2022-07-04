package kr.tangram.smartgym.ui.device

import DeviceRegisterRepository
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hwangjr.rxbus.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.base.BaseApplication
import kr.tangram.smartgym.base.BaseViewModel
import kr.tangram.smartgym.ble.SmartRopeManager
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.util.Define
import kr.tangram.smartgym.util.getNowDateFormat
import no.nordicsemi.android.support.v18.scanner.ScanResult
import org.koin.core.component.inject
import java.util.*

class DeviceViewModel(
): BaseViewModel() {

    private val deviceRegisterRepository: DeviceRegisterRepository by inject()

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

    var smartRopeManager : SmartRopeManager

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

                var deviceRegister = DeviceRegister(true, 100, false, "",
                    getNowDateFormat("yyyyMMddHHmmss"), scanResult.device.address,
                    scanResult.scanRecord?.deviceName, "",
                    type)

                deviceRegisterRepository.insertDevice(deviceRegister)
                RxBus.get().post(Define.BusEvent.DeviceState, "");

                Toast.makeText(BaseApplication.context, "등록 완료", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(BaseApplication.context, "이미 저장됨", Toast.LENGTH_SHORT).show()
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


}