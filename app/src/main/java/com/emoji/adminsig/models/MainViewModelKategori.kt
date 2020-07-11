package com.emoji.adminsig.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emoji.adminsig.services.ServiceBuilder
import com.skripsi.sigwam.model.Kategori
import com.skripsi.sigwam.model.KategoriResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModelKategori : AndroidViewModel(Application()) {

    private val destination =  MutableLiveData<ArrayList<Kategori>>()
    private val apiService = ServiceBuilder.create()

    fun setKategori(){
        val dataDestination = ArrayList<Kategori>()
        apiService.getKategori().enqueue(object : Callback<KategoriResponse> {
            override fun onFailure(call: Call<KategoriResponse>, t: Throwable) {
                Log.d("FailureLog", t.message)
            }

            override fun onResponse(call: Call<KategoriResponse>, response: Response<KategoriResponse>) {
                val dataDes = response.body()
                Log.d("ResponseLog", response.toString())
                dataDestination.addAll(dataDes!!.data)
                destination.postValue(dataDestination)

            }
        })
    }

    fun getKategori(): LiveData<ArrayList<Kategori>> {
        return destination
    }





}