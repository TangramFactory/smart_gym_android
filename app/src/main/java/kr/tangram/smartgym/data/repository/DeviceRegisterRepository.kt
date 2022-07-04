import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kr.tangram.smartgym.data.domain.model.DeviceRegister
import kr.tangram.smartgym.data.local.AppDatabase
import kr.tangram.smartgym.data.remote.RestApi
import kr.tangram.smartgym.data.remote.model.DeviceInfo
import kr.tangram.smartgym.data.remote.request.ReqDeviceLoad
import kr.tangram.smartgym.data.remote.response.BaseResponse
import kr.tangram.smartgym.data.remote.response.DeviceListResponse
import kr.tangram.smartgym.util.getNowDateFormat
import org.koin.core.component.KoinComponent
class DeviceRegisterRepository(
    private val restApi: RestApi
    , private val db: AppDatabase
) : KoinComponent {


    fun getDeviceList(): Observable<List<DeviceRegister>> =
        db.deviceRegisterDAO().getDeviceList()

    fun getDeviceList(identifier: String): Single<List<DeviceRegister>> =
        db.deviceRegisterDAO().getDeviceList(identifier)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())



    fun updateDeviceConnect(connect: Boolean, identifier: String)
    {
        db.deviceRegisterDAO().updateDeviceConnect(connect, getNowDateFormat("yyyyMMddHHmmss"), identifier)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun updateDeviceBattery(battery: Int, identifier: String)
    {
        db.deviceRegisterDAO().updateDeviceBattery(battery, identifier)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

    }

    fun updateDeviceVersion(version: Int, identifier: String)
    {
        db.deviceRegisterDAO().updateDeviceVersion(version, identifier)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun updateDeviceAuto(auto: Boolean, identifier: String)
    {
        db.deviceRegisterDAO().updateDeviceAuto(auto, identifier)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun updateDeviceName(name: String, identifier: String)
    {
        db.deviceRegisterDAO().updateDeviceName(name, identifier)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun deleteDevice(identifier: String)
    {
        db.deviceRegisterDAO().deleteDevice(identifier)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun insertDevice(deviceRegister: DeviceRegister)
    {
        db.deviceRegisterDAO().insertDevice(deviceRegister)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }


    fun updateDeviceAlas(deviceInfo: DeviceInfo) : Observable<BaseResponse>
        = restApi.updateDeviceAlias(deviceInfo)

    fun getDeviceLoadList(reqDeviceLoad : ReqDeviceLoad) : Observable<DeviceListResponse>
        = restApi.getDeviceLoadList(reqDeviceLoad)

}

