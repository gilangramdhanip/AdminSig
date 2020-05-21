package com.emoji.adminsig.activities

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emoji.adminsig.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class MapsTambah : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_tambah)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        supportActionBar?.hide()
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(-8.6064737,116.2000303))
            .zoom(10f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        if (mMap != null) {
            mMap.setOnMapClickListener(GoogleMap.OnMapClickListener { latLng ->
                mMap.clear()
                val geocoder = Geocoder(this@MapsTambah)
                val list: List<Address>
                list = try {
                    geocoder.getFromLocation(
                        latLng.latitude,
                        latLng.longitude, 1
                    )
                } catch (e: IOException) {
                    return@OnMapClickListener
                }
                val address: Address = list[0]

                val options = MarkerOptions()
                    .title("Tambah Lokasi ini")
                    .position(
                        LatLng(
                            latLng.latitude,
                            latLng.longitude
                        )
                    )
                val lokasi = LatLng(latLng.latitude, latLng.longitude)
                marker = mMap.addMarker(options)
                marker.tag = lokasi

            })

            mMap.setOnInfoWindowClickListener(object : GoogleMap.OnInfoWindowClickListener{
                override fun onInfoWindowClick(p0: Marker?) {

                    val desti = p0!!.tag as LatLng

                    val intent = Intent(this@MapsTambah, DestinationCreateActivity::class.java)
                    intent.putExtra(DestinationCreateActivity.EXTRA_CREATE, desti)
                    startActivity(intent)
                }
            })
        }
    }
}
