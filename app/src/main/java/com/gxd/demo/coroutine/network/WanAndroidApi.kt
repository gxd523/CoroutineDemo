package com.gxd.demo.coroutine.network

import com.gxd.demo.coroutine.network.data.BannerData
import com.gxd.demo.coroutine.network.data.CheckTokenRequest
import com.gxd.demo.coroutine.network.data.LoginData
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface WanAndroidApi {
    @GET("banner/{path}")
    suspend fun requestBannerList(@Path("path") path: String): List<BannerData>

    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(@Field("username") username: String, @Field("password") password: String): LoginData

    @POST("https://meta-forest-dapp-backend-test-10052-8080.apps-qa.danlu.netease.com/api/login/checkToken")
    suspend fun checkToken(
        @Body checkTokenRequest: CheckTokenRequest, @Header("timestamp") timestamp: Long = System.currentTimeMillis()
    ): Boolean
}