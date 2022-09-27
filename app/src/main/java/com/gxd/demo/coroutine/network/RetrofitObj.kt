package com.gxd.demo.coroutine.network

import com.gxd.demo.coroutine.network.transform.ResponseConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObj {
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .addConverterFactory(ResponseConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}