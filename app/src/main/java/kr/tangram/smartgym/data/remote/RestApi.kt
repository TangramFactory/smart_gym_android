package kr.tangram.smartgym.data.remote


import io.reactivex.Observable
import io.reactivex.Single
import kr.tangram.smartgym.data.remote.model.DeviceInfo
import kr.tangram.smartgym.data.remote.model.UserExistsResult
import kr.tangram.smartgym.data.remote.model.UserRegResult
import kr.tangram.smartgym.data.remote.request.ReqDeviceLoad
import kr.tangram.smartgym.data.remote.response.BaseResponse
import kr.tangram.smartgym.data.remote.response.DeviceListResponse
import retrofit2.http.*
import java.util.*

interface RestApi {

    @GET("repos/{owner}/{repo}/contributors")
    fun getObContributors(@Path("owner") owner: String, @Path("repo") repo: String) : Single<List<String>>

    @FormUrlEncoded
    @POST("user/userExists")
    fun getUserExists(@Field("email") email: String) : Observable<UserExistsResult>

    @FormUrlEncoded
    @POST("user/userReg")
    fun getUserReg(@Field("uid") uid: String,
                      @Field("email") email: String,
                      @Field("country") country: String = Locale.getDefault().country.toString(),
                      @Field("timezone") timezone: String = TimeZone.getDefault().id.toString()
    ) : Observable<UserRegResult>


    @POST("device/deviceSaveAlias")
    fun updateDeviceAlias(@Body deviceInfo: DeviceInfo) : Observable<BaseResponse>

    @POST("device/deviceLoadList")
    fun getDeviceLoadList(@Body reqDeviceLoad: ReqDeviceLoad) : Observable<DeviceListResponse>
}