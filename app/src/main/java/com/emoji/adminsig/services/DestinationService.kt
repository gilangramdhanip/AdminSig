package com.emoji.adminsig.services

import com.emoji.adminsig.models.*
import retrofit2.Call
import retrofit2.http.*

interface DestinationService {

    @GET("destination")
    fun getDestinationList(): Call<DestinationResponse>

    @GET("destination")
    fun getDestination(@Path("id_destination") id_destination: Int): Call<DestinationResponse>

    @POST("destination")
    fun addDestination(@Body newDestination: Destination): Call<DestinationResponse>

    @POST("kabupaten")
    fun getKabupaten(): Call<Array<Kabupaten>>

    @FormUrlEncoded
    @POST("kecamatan")
    fun getKecamatan(@Field("id_kabupaten") id_kabupaten: String): Call<Array<Kecamatan>>


    @FormUrlEncoded
    @PUT("destination")
    fun updateDestination(
        @Field("id_destination") id_destination: Int,
        @Field("name_destination") name: String,
        @Field("lat_destination") lat: String,
        @Field("lng_destination") lng: String,
        @Field("address_destination") address: String,
        @Field("desc_destination") description: String,
        @Field("category_destination") cat: String,
        @Field("img_destination") image: String,
        @Field("id_kecamatan") id_kec: String,
        @Field("id_admin") id_admin: String
    ): Call<DestinationResponse>

    @FormUrlEncoded
    @HTTP(  method = "DELETE", path = "destination", hasBody = true)
    fun deleteDestination(
        @Field("id_destination") id_destination: Int
    ): Call<DeleteResponse>

}