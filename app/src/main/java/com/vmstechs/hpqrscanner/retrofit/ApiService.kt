package com.fmcg.retrofit

import com.vmstechs.hpqrscanner.utils.NetworkConst.BASE_URL
import com.vmstechs.hpqrscanner.retrofit.RetrofitAPI
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiService {

    fun getNetworkService(): RetrofitAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitAPI::class.java)
    }



    fun getAuthNetworkService(token: String): RetrofitAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }.build())
            .build()
            .create(RetrofitAPI::class.java)
    }
}