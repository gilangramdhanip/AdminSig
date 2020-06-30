package com.skripsi.sigwam.service

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ServiceBuilder {

    val okHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    var gson = GsonBuilder()
        .setLenient()
        .create()

    fun create(): DestinationService {
        val retrofit= Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://siwita-lombok.000webhostapp.com/rest_api/rest-server-sig/api/")
            .build()
        return retrofit.create(DestinationService::class.java)
    }
}