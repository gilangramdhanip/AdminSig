package com.skripsi.sigwam.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skripsi.sigwam.service.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : AndroidViewModel(Application()) {

    private val destination =  MutableLiveData<ArrayList<Destination>>()
    private val apiService = ServiceBuilder.create()

    fun setDestination(){
        val dataDestination = ArrayList<Destination>()
        apiService.getDestinationList().enqueue(object : Callback<DestinationResponse> {
            override fun onFailure(call: Call<DestinationResponse>, t: Throwable) {
                Log.d("FailureLog", t.message)
            }

            override fun onResponse(call: Call<DestinationResponse>, response: Response<DestinationResponse>) {
                val dataDes = response.body()
                Log.d("ResponseLog", response.toString())
                dataDestination.addAll(dataDes!!.data)
                destination.postValue(dataDestination)
            }
        })
    }

    fun getDestination(): LiveData<ArrayList<Destination>> {
        return destination
    }





}