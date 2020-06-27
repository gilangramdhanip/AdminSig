package com.skripsi.sigwam.service

import com.skripsi.sigwam.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface DestinationService {

    @GET("destination")
    fun getDestinationList(): Call<DestinationResponse>

    @GET("destination")
    fun getDestinationbyId(
        @Query ("id_wisatawan") id_wisatawan : String
    ): Call<DestinationResponse>

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
    @PUT("wisatawan")
    fun updateWisatawan(
        @Field("id_wisatawan") id_wisatawan: String,
        @Field("email") email: String,
        @Field("nama") nama: String,
        @Field("password") password: String
    ): Call<WisatawanResponse>

    @Multipart
    @POST("update")
    fun updateDestination(
        @Part("id_destination") id_destination: Int,
        @PartMap partmap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part img_destination: MultipartBody.Part?
    ): Call<DestinationResponse>

    @Multipart
    @POST("destination")
    fun addDestination(
        @PartMap partmap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part img_destination: MultipartBody.Part?
    ): Call<DestinationResponse>

    @FormUrlEncoded
    @POST("wisatawan")
    fun signin(
        @Field("email") email: String,
        @Field("password") password: String): Call<LoginResponse>

    @FormUrlEncoded
    @POST("lupapassword")
    fun lupaPassword(
        @Field("email") email: String
    ): Call<WisatawanResponse>

    @FormUrlEncoded
    @PUT("lupapassword")
    fun gantiPassword(
        @Field("id_wisatawan") id_wisatawan: String,
        @Field("email") email: String,
        @Field("nama") nama: String,
        @Field("password") password: String): Call<WisatawanResponse>

    @POST("registration")
    fun regisWisatawan(@Body newWisatawan: Wisatawan): Call<WisatawanResponse>

    @FormUrlEncoded
    @HTTP(  method = "DELETE", path = "api/destination", hasBody = true)
    fun deleteDestination(
        @Field("id_destination") id_destination: Int
    ): Call<DeleteResponse>

}