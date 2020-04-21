package com.skripsi.sigwam.service

import com.skripsi.sigwam.model.*
import retrofit2.Call
import retrofit2.http.*

interface DestinationService {

    @GET("destination")
    fun getDestinationList(): Call<DestinationResponse>

    @GET("destination")
    fun getFilterKategori(@Query("id_kategori") id_kategori: String): Call<DestinationResponse>

    @POST("destination")
    fun addDestination(@Body newDestination: Destination): Call<DestinationResponse>

    @GET("kabupaten")
    fun getKabupaten(): Call<KabupatenResponse>

    @GET("kategori")
    fun getKategori(): Call<KategoriResponse>

    @GET("kecamatan")
    fun getKecamatan(@Query("id_kabupaten") id_kabupaten: String): Call<KecamatanResponse>

    @GET("destination")
    fun getKecamatanbyid(
        @Query("id_kabupaten") id_kabupaten: String,
        @Query("id_kecamatan") id_kecamatan: String): Call<KecamatanResponse>

    @FormUrlEncoded
    @PUT("destination")
    fun updateDestination(
        @Field("id_destination") id_destination: Int,
        @Field("name_destination") name: String,
        @Field("lat_destination") lat: String,
        @Field("lng_destination") lng: String,
        @Field("address_destination") address: String,
        @Field("desc_destination") description: String,
        @Field("img_destination") image: String,
        @Field("id_kategori") cat: String,
        @Field("id_kabupaten") id_kab: String,
        @Field("id_kecamatan") id_kec: String

    ): Call<DestinationResponse>

}