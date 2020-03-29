package com.emoji.adminsig.activities

import android.Manifest
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.emoji.adminsig.R
import com.emoji.adminsig.helpers.LocationFormatter
import com.emoji.adminsig.models.Destination
import com.emoji.adminsig.models.DestinationResponse
import com.emoji.adminsig.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_destiny_create.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DestinationCreateActivity : AppCompatActivity(), LocationFormatter {

    companion object {
        private const val PERMISSION_REQUEST = 11
    }

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var location: Location? = null
    private var locationManager: LocationManager? = null

    private val islocationAvailable: Boolean
        get() = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)?:false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destiny_create)

//        setSupportActionBar(toolbar)
        val context = this

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tambahdata()


        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==0){
            location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            latitude = location?.latitude ?:0.0
            longitude = location?.longitude ?:0.0
        }

//        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10f, locationListener)
        setUpLayout()


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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_REQUEST -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this@DestinationCreateActivity, "Nyalakan GPS", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?){
            if(location!=null){
                latitude = location.latitude
                longitude = location.longitude
            }
        }


        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?){}

        override fun onProviderEnabled(provider: String?) {
            checkGPSLocation()
        }


        override fun onProviderDisabled(p0: String?) {
            checkGPSLocation()
        }
    }

    private fun setUpLayout(){

        et_latitude.text = Editable.Factory.getInstance().newEditable(latitude.toString())
        et_longitude.text = Editable.Factory.getInstance().newEditable(longitude.toString())
        et_address.text = Editable.Factory.getInstance().newEditable(getLocation(this, latitude, longitude))
    }

    private fun checkGPSLocation(): Boolean{
        if(!islocationAvailable) showNoGpsDialog()
        return islocationAvailable
    }

    private fun showNoGpsDialog(){
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.gps_belum_aktif))
            .setCancelable(false)
            .setPositiveButton("Aktifkan"){
                    _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Tidak"){
                    dialog, _ -> dialog.cancel()
            }
            .create()
            .show()
    }




}
