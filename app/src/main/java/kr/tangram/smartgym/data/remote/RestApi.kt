package kr.tangram.smartgym.data.remote

import io.reactivex.Single
import kr.tangram.smartgym.data.model.UserExistsResult
import kr.tangram.smartgym.data.model.UserRegResult
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

interface RestApi {

    @GET("repos/{owner}/{repo}/contributors")
    fun getObContributors(@Path("owner") owner: String, @Path("repo") repo: String) : Single<List<String>>

    @GET("user/userReg")
    fun getUserReg(@Field("email") email: String) : UserRegResult

    @GET("user/userExists")
    fun getUserExists(@Field("uid") uid: String,
                      @Field("email") email: String,
                      @Field("country") country: String = Locale.getDefault().country.toString(),
                      @Field("timezone") timezone: String = TimeZone.getDefault().id.toString()
    ) : UserExistsResult

}