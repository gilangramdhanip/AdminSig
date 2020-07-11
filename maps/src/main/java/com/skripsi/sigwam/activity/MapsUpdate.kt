package com.skripsi.sigwam.activity

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.skripsi.sigwam.R
import com.skripsi.sigwam.model.Destination
import kotlinx.android.synthetic.main.activity_maps_update.*
import java.io.IOException

class MapsUpdate : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var marker: Marker

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
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
        fusedLocationProviderClient = FusedLocationProviderClient(application)

        val toolbar: Toolbar? = findViewById<Toolbar>(R.id.profileToolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)

        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        destination = intent.getParcelableExtra(EXTRA_UPDATE) as Destination
        destination.id_destination
        latlng = LatLng(destination.lat_destination!!.toDouble(), destination.lng_destination!!.toDouble())

        sv_location.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val location = sv_location.query.toString()
                var addressList : List<Address>? = null

                if(location!=null || !location.equals("")){
                    val geocoder : Geocoder = Geocoder(this@MapsUpdate)
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
                        val lokasi = Destination(destination.id_destination, destination.name_destination,
                            latLng.latitude.toString(), latLng.longitude.toString(), address.getAddressLine(0).toString(), destination.desc_destination, destination.id_kategori, destination.img_destination, destination.id_kabupaten, destination.id_kecamatan, destination.jambuka, destination.jamtutup)
                        marker = mMap.addMarker(options)
                        marker.tag = lokasi
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20F))

                        mMap.setOnInfoWindowClickListener { p0 ->
                            val desti = p0!!.tag as Destination
                            Log.d("Response desti", desti.toString())

                            val intent = Intent(this@MapsUpdate, DestinationDetail::class.java)
                            intent.putExtra(DestinationDetail.EXTRA_DETAIl, desti)
                            startActivity(intent)
                        }
                    }else{
                        Toast.makeText(this@MapsUpdate, "Data tidak ditemukan atau silahkan tulis lokasi dengan lengkap", Toast.LENGTH_SHORT).show()
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

                val intent = Intent(this@MapsUpdate, DestinationDetail::class.java)
                intent.putExtra(DestinationDetail.EXTRA_DETAIl, desti)
                startActivity(intent)
            }
        }
    }
}
