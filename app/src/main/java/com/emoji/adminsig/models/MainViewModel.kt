package com.emoji.adminsig.models

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emoji.adminsig.services.ServiceBuilder
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