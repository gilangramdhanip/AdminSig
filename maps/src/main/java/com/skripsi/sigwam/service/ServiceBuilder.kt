package com.skripsi.sigwam.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    fun create(): DestinationService {
        val retrofit= Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://siwita.000webhostapp.com/rest_api/rest-server-sig/api/")
            .build()
        return retrofit.create(DestinationService::class.java)
    }
}