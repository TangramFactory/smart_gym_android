package kr.tangram.smartgym.data.remote


import io.reactivex.Observable
import kr.tangram.smartgym.data.remote.model.*
import kr.tangram.smartgym.data.remote.request.ReqDeviceLoad
import kr.tangram.smartgym.data.remote.response.BaseResponse
import kr.tangram.smartgym.data.remote.response.DeviceListResponse
import retrofit2.http.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@Singleton
interface BleRestApi {
    @POST("device/deviceSaveAlias")
    fun updateDeviceAlias(@Body deviceInfo: DeviceInfo) : Observable<BaseResponse>

    @POST("device/deviceLoadList")
    fun getDeviceLoadList(@Body reqDeviceLoad: ReqDeviceLoad) : Observable<DeviceListResponse>
}