package com.skripsi.sigwam.model

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skripsi.sigwam.service.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModelWisatawan : AndroidViewModel(Application()) {

    private val destination =  MutableLiveData<ArrayList<Destination>>()
    private val apiService = ServiceBuilder.create()

    fun setDestination(id_wisatawan : String){
        val dataDestination = ArrayList<Destination>()
        apiService.getDestinationbyId(id_wisatawan).enqueue(object : Callback<DestinationResponse> {
            override fun onFailure(call: Call<DestinationResponse>, t: Throwable) {
                Log.d("FailureLog", t.message)
            }

            override fun onResponse(call: Call<DestinationResponse>, response: Response<DestinationResponse>) {
                if(response.isSuccessful){
                    val dataDes = response.body()
                    Log.d("ResponseLog", response.toString())
                    dataDestination.addAll(dataDes!!.data)
                    destination.postValue(dataDestination)
                }else{
                    Log.d("else response", response.toString())
//                    Toast.makeText(getApplication(), "Gagal", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun getDestination(): LiveData<ArrayList<Destination>> {
        return destination
    }





}