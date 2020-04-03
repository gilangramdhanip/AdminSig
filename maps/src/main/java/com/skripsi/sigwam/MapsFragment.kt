package com.skripsi.sigwam

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.skripsi.sigwam.model.Destination
import com.skripsi.sigwam.model.DestinationResponse
import com.skripsi.sigwam.model.MainViewModel
import com.skripsi.sigwam.service.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class MapsFragment : Fragment(), OnMapReadyCallback, PermissionListener {

    companion object {
        const val REQUEST_CHECK_SETTINGS = 43
        private const val TAG = "MapsFragment"
        var EXTRA_NAME = "extra_name"
    }

    private lateinit var googleMap: GoogleMap
    private val apiService = ServiceBuilder.create()
    private lateinit var destination : Array<Destination>
    private lateinit var mainViewModel : MainViewModel
    private lateinit var location : Destination
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_maps, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager?.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        fusedLocationProviderClient = FusedLocationProviderClient(requireActivity())
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map?: return

        loadDestination()

        if (isPermissionGiven()){
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            getCurrentLocation()
        } else {
            givePermission()
        }

    }

    private fun isPermissionGiven(): Boolean{
        return ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun givePermission() {
        Dexter.withActivity(activity)
            .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(this)
            .check()
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        getCurrentLocation()
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest?,
        token: PermissionToken?
    ) {
        token!!.continuePermissionRequest()
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Toast.makeText(requireContext(), "Permission required for showing location", Toast.LENGTH_LONG).show()
        activity?.finish()
    }

    private fun getCurrentLocation() {

        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        val result = LocationServices.getSettingsClient(requireActivity()).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent){
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(activity,
                            MapsActivity.REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { }
                }
            }
        }
    }

    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful && task.result != null) {
                    val mLastLocation = task.result

                    var address = "Alamat tidak ditemukan"

                    val gcd = Geocoder(requireContext(), Locale.getDefault())
                    val addresses: List<Address>
                    try {
                        addresses = gcd.getFromLocation(mLastLocation!!.latitude, mLastLocation.longitude, 1)
                        if (addresses.isNotEmpty()) {
                            address = addresses[0].getAddressLine(0)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

//                    val icon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.ic_person_pin_circle_black_24dp))
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(mLastLocation!!.latitude, mLastLocation.longitude))
                            .title("Lokasi terkini")
                            .snippet(address)
                    )

                    val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(mLastLocation.latitude, mLastLocation.longitude))
                        .zoom(10f)
                        .build()
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                } else {
                    Toast.makeText(requireContext(), "Lokasi terkini tidak ditemukan", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            MapsActivity.REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    getCurrentLocation()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun loadDestination(){
        apiService.getDestinationList().enqueue(object : Callback<DestinationResponse> {
            override fun onFailure(call: Call<DestinationResponse>, t: Throwable) {
                Toast.makeText(context, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
            }

            override fun onResponse(
                call: Call<DestinationResponse>,
                response: Response<DestinationResponse>
            ) {
                if(response.isSuccessful){
                    destination = response.body()!!.data
                    val litdes = ArrayList<Destination>(destination.size)

                    destination.forEach {
//                        litdes.add(it.id_destination)
                        googleMap.addMarker(MarkerOptions()
                            .position(LatLng(it.lat_destination!!.toDouble(), it.lng_destination!!.toDouble()))
                            .title(it.name_destination)
                            .snippet(it.address_destination)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        )

                        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    }

                    Log.d("onResponse", response.toString())
                }

                else{
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(context, "Gagal mengambil spinner", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }



}
