package com.emoji.adminsig.activities

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.emoji.adminsig.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException
import java.util.*


class MapsTambah : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var mMap: GoogleMap
    private lateinit var marker: Marker
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    companion object {
        const val REQUEST_CHECK_SETTINGS = 43
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_tambah)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = FusedLocationProviderClient(application)
        val toolbar: Toolbar? = findViewById<Toolbar>(R.id.profileToolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)

        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
            mMap.setPadding(0,0,0,0)
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
            mMap.uiSettings.isZoomControlsEnabled = true

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
                val lokasi =  address

                marker = mMap.addMarker(options)
                marker.tag = lokasi

            })

            mMap.setOnInfoWindowClickListener { p0 ->
                val desti = p0!!.tag as Address

                val intent = Intent(this@MapsTambah, DestinationCreateActivity::class.java)
                intent.putExtra(DestinationCreateActivity.EXTRA_CREATE, desti)
                startActivity(intent)
            }
        }
    }
}
