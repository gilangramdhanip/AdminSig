package com.emoji.adminsig.services

import com.emoji.adminsig.models.*
import com.skripsi.sigwam.model.KategoriResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface DestinationService {

    @GET("api/destination")
    fun getDestinationList(): Call<DestinationResponse>

    @GET("api/destination")
    fun getDestination(@Path("id_destination") id_destination: Int): Call<DestinationResponse>

    @Multipart
    @POST("api/destination")
    fun addDestination(
        @PartMap partmap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part img_destination: MultipartBody.Part
    ): Call<DestinationResponse>

    @GET("api/kabupaten")
    fun getKabupaten(): Call<KabupatenResponse>

    @GET("api/kategori")
    fun getKategori(): Call<KategoriResponse>

    @GET("api/kecamatan")
    fun getKecamatan(@Query("id_kabupaten") id_kabupaten: String): Call<KecamatanResponse>

    @GET("api/destination")
    fun getKecamatanbyid(
        @Query("id_kabupaten") id_kabupaten: String,
        @Query("id_kecamatan") id_kecamatan: String): Call<KecamatanResponse>


    @FormUrlEncoded
    @PUT("api/destination")
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

    @FormUrlEncoded
    @HTTP(  method = "DELETE", path = "api/destination", hasBody = true)
    fun deleteDestination(
        @Field("id_destination") id_destination: Int
    ): Call<DeleteResponse>

}