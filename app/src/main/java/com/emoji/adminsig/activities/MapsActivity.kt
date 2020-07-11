package com.emoji.adminsig.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emoji.adminsig.R
import com.emoji.adminsig.models.Destination
import com.emoji.adminsig.services.ServiceBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap

class MapsActivity : AppCompatActivity(){

    companion object {
        const val REQUEST_CHECK_SETTINGS = 43
    }

    private lateinit var googleMap: GoogleMap
    private val apiService = ServiceBuilder.create()
    private lateinit var destination : Array<Destination>
    lateinit var location : Destination
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar?.hide()
        supportFragmentManager.beginTransaction().replace(R.id.maps_fragm, MapsFragment()).commit()
    }

}