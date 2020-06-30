package com.emoji.adminsig.services

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    val gson = GsonBuilder()
        .setLenient()
        .create()

    fun create(): DestinationService{
        val retrofit= Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://siwita-lombok.000webhostapp.com/rest_api/rest-server-sig/")
            .build()
        return retrofit.create(DestinationService::class.java)
    }
}