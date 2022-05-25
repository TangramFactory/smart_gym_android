package kr.tangram.smartgym.data.remote

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface RestApi {

    @GET("repos/{owner}/{repo}/contributors")
    fun getObContributors(@Path("owner") owner: String, @Path("repo") repo: String) : Single<List<String>>
}