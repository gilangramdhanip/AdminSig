package com.skripsi.sigwam.activity

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.skripsi.sigwam.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps_tambah.*
import java.io.IOException


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

        sv_location.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val location = sv_location.query.toString()
                var addressList : List<Address>? = null

                if(location!=null || !location.equals("")){
                    val geocoder : Geocoder = Geocoder(this@MapsTambah)
                    try{
                        addressList = geocoder.getFromLocationName(location, 10)

                    }catch (e: IOException){
                        e.printStackTrace()
                        e.toString()
                    }
                    if(addressList!!.isNotEmpty()) {
                        val address = addressList!![0]
                        val latLng = LatLng(address.latitude, address.longitude)
                        val options = MarkerOptions()
                            .title("tambahkan $location")
                            .position(
                                LatLng(
                                    latLng.latitude,
                                    latLng.longitude
                                )
                            )
                        val lokasi = address

                        marker = mMap.addMarker(options)
                        marker.tag = lokasi
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20F))

                        mMap.setOnInfoWindowClickListener { p0 ->
                            val desti = p0!!.tag as Address

                            val intent = Intent(this@MapsTambah, DestinationCreateActivity::class.java)
                            intent.putExtra(DestinationCreateActivity.EXTRA_CREATE, desti)
                            intent.putExtra("NAMA", location)
                            startActivity(intent)
                        }
                    }else{
                        Toast.makeText(this@MapsTambah, "Data tidak ditemukan atau silahkan tulis lokasi dengan lengkap", Toast.LENGTH_SHORT).show()
                    }
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })

    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
            mMap.setPadding(0,200,0,0)
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

            mMap.setOnInfoWindowClickListener(object : GoogleMap.OnInfoWindowClickListener{
                override fun onInfoWindowClick(p0: Marker?) {

                    val desti = p0!!.tag as Address

                    val intent = Intent(this@MapsTambah, DestinationCreateActivity::class.java)
                    intent.putExtra(DestinationCreateActivity.EXTRA_CREATE, desti)
                    startActivity(intent)
                }
            })
        }
    }
}
