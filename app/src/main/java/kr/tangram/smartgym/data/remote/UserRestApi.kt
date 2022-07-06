package kr.tangram.smartgym.data.remote


import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.Single
import kr.tangram.smartgym.data.remote.response.JuniorResponse
import kr.tangram.smartgym.data.remote.response.UserExistsResponse
import kr.tangram.smartgym.data.remote.response.UserLoginResponse
import kr.tangram.smartgym.data.remote.response.BaseResponse
import retrofit2.http.*
import java.util.*
import javax.inject.Singleton

@Singleton
interface UserRestApi {

    @GET("repos/{owner}/{repo}/contributors")
    fun getObContributors(@Path("owner") owner: String, @Path("repo") repo: String) : Single<List<String>>

    @FormUrlEncoded
    @POST("user/userExists")
    fun getUserExists(@Field("email") email: String) : Observable<UserExistsResponse>

    @FormUrlEncoded
    @POST("user/userLoadJuniorList")
    fun getJuniorList(@Field("uid") uid: String) : Observable<JuniorResponse>


    @FormUrlEncoded
    @POST("user/userReg")
    fun getUserReg(@Field("uid") uid: String,
                   @Field("email") email: String,
                   @Field("name") name: String,
                   @Field("gender") gender : Int,
                   @Field("birthday") birthday: String,
                   @Field("juniorYn") juniorYn: String,
                   @Field("parentsUid") parentsUid: String,
                   @Field("country") country: String = Locale.getDefault().country.toString(),
                   @Field("language") language: String = Locale.getDefault().language.toString(),
                   @Field("timezone") timezone: String = TimeZone.getDefault().id.toString(),
                   @Field("timezoneOffset") timezoneOffset: Int = TimeZone.getDefault().rawOffset,

                   ) : Observable<BaseResponse>


    @FormUrlEncoded
    @POST("user/userModify")
    fun updateUserProfile(
        @Field("uid") uid: String,
        @Field("nickname") nickname: String? = null,
        @Field("introduce") introduce: String? = null,
        @Field("hwUnit") hwUnit: String? = null,
        @Field("height") height: String? = null,
        @Field("weight") weight: String? = null,
        @Field("dailyGoal") dailyGoal: String? = null
    ):Observable<BaseResponse>

    @FormUrlEncoded
    @POST("user/userModifyJunior")
    fun modifyJuniorProfile(
        @Field("uid") uid: String,
        @Field("name") name: String,
        @Field("gender") gender : Int,
        @Field("birthday") birthday: String
    ):Observable<BaseResponse>

    @POST("user/userLogin")
    fun updateUserLogin(@Body auth: JsonObject): Observable<UserLoginResponse>

}