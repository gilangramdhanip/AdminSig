package com.emoji.adminsig.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.emoji.adminsig.R
import com.emoji.adminsig.helpers.LocationFormatter
import com.emoji.adminsig.models.Destination
import com.emoji.adminsig.models.DestinationResponse
import com.emoji.adminsig.services.ServiceBuilder
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_destiny_create.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DestinationCreateActivity : AppCompatActivity() {
    val RequestPermissionCode = 1
    var mLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destiny_create)

//        setSupportActionBar(toolbar)

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        tambahdata()

    }

    private fun tambahdata(){
        btn_add.setOnClickListener {
            val newDestination = Destination(taskId)
            newDestination.name_destination = et_name.text.toString()
            newDestination.lat_destination = et_latitude.text.toString()
            newDestination.lng_destination = et_longitude.text.toString()
            newDestination.address_destination = et_address.text.toString()
            newDestination.desc_destination = et_description.text.toString()
            newDestination.img_destination = et_image.text.toString()
            newDestination.category_destination = et_category.text.toString()
            newDestination.id_admin = et_admin.text.toString()
            newDestination.id_kecamatan = et_kecamatan.text.toString()


            val destinationService = ServiceBuilder.create()
            val requestCall = destinationService.addDestination(newDestination)

            requestCall.enqueue(object: Callback<DestinationResponse> {

                override fun onResponse(call: Call<DestinationResponse>, response: Response<DestinationResponse>) {
                    if (response.isSuccessful) {
                        finish() // Move back to DestinationListActivity
                        var newlyCreatedDestination = response.body() // Use it or ignore it
                        Toast.makeText(applicationContext, "Successfully Added", Toast.LENGTH_SHORT).show()
                        Log.d("ResponseLog", response.toString())
                    } else {
                        Toast.makeText(applicationContext, "Failed to add item", Toast.LENGTH_SHORT).show()
                        Log.d("FailureLog", response.toString())
                    }
                }

                override fun onFailure(call: Call<DestinationResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Failed to add item", Toast.LENGTH_SHORT).show()
                    Log.d("FailureLog", t.message)
                }
            })
        }
    }

    fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    mLocation = location
                    if (location != null) {
                        et_latitude.text =  Editable.Factory.getInstance().newEditable(location.latitude.toString())
                        et_longitude.text = Editable.Factory.getInstance().newEditable(location.longitude.toString())
                    }
                }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            RequestPermissionCode
        )
        this.recreate()
    }
}

