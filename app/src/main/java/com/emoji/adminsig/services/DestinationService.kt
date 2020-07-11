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
    fun getFilterKategori(@Query("id_kategori") id_kategori: String): Call<DestinationResponse>

    @GET("api/destination")
    fun getDestinationListByStatus(@Query("status") status: String ): Call<DestinationResponse>

    @Multipart
    @POST("api/destination")
    fun addDestination(
        @PartMap partmap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part img_destination: MultipartBody.Part?
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
    @POST("api/user")
    fun signin(
        @Field("username") email: String,
           @Field("password") password: String): Call<LoginResponse>

    @Multipart
    @POST("api/update")
    fun updateDestination(
        @Part("id_destination") id_destination: Int,
        @PartMap partmap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part img_destination: MultipartBody.Part?
    ): Call<DestinationResponse>

    @FormUrlEncoded
    @HTTP(  method = "DELETE", path = "api/destination", hasBody = true)
    fun deleteDestination(
        @Field("id_destination") id_destination: Int
    ): Call<DeleteResponse>

}