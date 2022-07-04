package kr.tangram.smartgym.data.remote


import io.reactivex.Observable
import kr.tangram.smartgym.data.remote.model.*
import retrofit2.http.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@Singleton
interface WorkOutRestApi {

    @POST("jump/jumpSave")
    fun jumpSave(@Body auth: JumpSaveObject):Observable<BaseResult>

    @FormUrlEncoded
    @POST("jump/jumpLoadDay")
    fun jumpLoadDay(@Field("uid") uid: String,
                    @Field("jumpYmd") jumpYmd: String = SimpleDateFormat("yyyyMMdd").format(Date(System.currentTimeMillis()))):Observable<JumpLoadDayResult>

}