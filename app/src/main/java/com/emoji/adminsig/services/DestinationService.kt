package com.emoji.adminsig.services

import com.emoji.adminsig.models.DeleteResponse
import com.emoji.adminsig.models.Destination
import com.emoji.adminsig.models.DestinationResponse
import retrofit2.Call
import retrofit2.http.*

interface DestinationService {

    @GET("destination")
    fun getDestinationList(): Call<DestinationResponse>

    @GET("destination")
    fun getDestination(@Path("id_destination") id_destination: Int): Call<DestinationResponse>

    @POST("destination")
    fun addDestination(@Body newDestination: Destination): Call<DestinationResponse>

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
        @Field("id_kecamatan") id_kecamatan: String,
        @Field("id_admin") id_admin: String
    ): Call<DestinationResponse>

    @FormUrlEncoded
    @HTTP(  method = "DELETE", path = "destination", hasBody = true)
    fun deleteDestination(
        @Field("id_destination") id_destination: Int
    ): Call<DeleteResponse>

}