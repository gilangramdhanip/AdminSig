package com.emoji.adminsig.activities

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.emoji.adminsig.R
import com.emoji.adminsig.models.Destination
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.io.IOException

class MapsUpdate : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var marker: Marker

    companion object {
        const val EXTRA_UPDATE = "extra_update"
    }

    lateinit var latlng : LatLng

    private lateinit var destination : Destination
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_update)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        supportActionBar?.hide()

        destination = intent.getParcelableExtra(EXTRA_UPDATE) as Destination
        destination.id_destination
        latlng = LatLng(destination.lat_destination!!.toDouble(), destination.lng_destination!!.toDouble())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.addMarker(MarkerOptions()
            .position
                (latlng)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))

        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(-8.6064737,116.2000303))
            .zoom(10f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        if (mMap != null) {
            mMap.setOnMapClickListener(GoogleMap.OnMapClickListener { latLng ->
                mMap.clear()
                val geocoder = Geocoder(this@MapsUpdate)
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
                val lokasi = Destination(destination.id_destination, destination.name_destination,
                    latLng.latitude.toString(), latLng.longitude.toString(), address.getAddressLine(0).toString(), destination.desc_destination, destination.id_kategori, destination.img_destination, destination.id_kabupaten, destination.id_kecamatan, destination.jambuka, destination.jamtutup)
                marker = mMap.addMarker(options)
                marker.tag = lokasi

            })

            mMap.setOnInfoWindowClickListener { p0 ->
                val desti = p0!!.tag as Destination
                Log.d("Response desti", desti.toString())

                val intent = Intent(this@MapsUpdate, DestinationDetailActivity::class.java)
                intent.putExtra(DestinationDetailActivity.EXTRA_DETAIl, desti)
                startActivity(intent)
            }
        }
    }
}
