package com.skripsi.sigwam

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.skripsi.sigwam.adapter.KategoriAdapter
import com.skripsi.sigwam.model.Destination
import com.skripsi.sigwam.model.DestinationResponse
import com.skripsi.sigwam.model.Kategori
import com.skripsi.sigwam.model.MainViewModelKategori
import com.skripsi.sigwam.service.ServiceBuilder
import kotlinx.android.synthetic.main.bottom_sheet_category.view.*
import kotlinx.android.synthetic.main.dialog_item.*
import kotlinx.android.synthetic.main.dialog_item.view.*
import kotlinx.android.synthetic.main.fragment_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class MapsFragment : Fragment(), OnMapReadyCallback, PermissionListener{

    companion object {

        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LONG = "extra_lng"
    }
    private lateinit var dialog: BottomSheetDialog
    private lateinit var googleMap: GoogleMap
    private val apiService = ServiceBuilder.create()
    lateinit var destination : List<Destination>
    lateinit var cat : List<Kategori>
    lateinit var mainViewModelKategori: MainViewModelKategori
    lateinit var kategoriAdapter: KategoriAdapter
    private val kategori = ArrayList<Kategori>()
    private lateinit var category : Kategori
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

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fusedLocationProviderClient = FusedLocationProviderClient(requireActivity())

    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map?: return

        if (isPermissionGiven()){
            googleMap.setPadding(0,200,0,0)
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            getCurrentLocation()

            loadDestination()
            loadKategoriMenu()

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

        val personBitmap = BitmapFactory.decodeResource(resources, R.drawable.personmarker)
        val personMarker = Bitmap.createScaledBitmap(personBitmap, 100, 100, false)
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

                    googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(mLastLocation!!.latitude, mLastLocation.longitude))
                            .title("Lokasi terkini")
                            .snippet(address)
                            .icon(BitmapDescriptorFactory.fromBitmap(personMarker))
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
        var markerOption = MarkerOptions()
        val beachBitmap = BitmapFactory.decodeResource(resources, R.drawable.beach)
        val beachMarker = Bitmap.createScaledBitmap(beachBitmap, 90, 90, false)
        val hillBitmap = BitmapFactory.decodeResource(resources, R.drawable.mountain)
        val hillMarker = Bitmap.createScaledBitmap(hillBitmap, 90, 90, false)
        val mountainBitmap = BitmapFactory.decodeResource(resources, R.drawable.mountain)
        val mountainMarker = Bitmap.createScaledBitmap(mountainBitmap, 90, 90, false)
        val waterfallBitmap = BitmapFactory.decodeResource(resources, R.drawable.waterfall)
        val waterfallMarker = Bitmap.createScaledBitmap(waterfallBitmap, 90, 90, false)
        apiService.getDestinationList().enqueue(object : Callback<DestinationResponse> {
            override fun onFailure(call: Call<DestinationResponse>, t: Throwable) {
                Toast.makeText(context, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<DestinationResponse>,
                response: Response<DestinationResponse>
            ) {
                if(response.isSuccessful){
                    destination = response.body()!!.data

                    destination.forEach {
                        val lat = it.lat_destination!!.toDouble()
                        val lng = it.lng_destination!!.toDouble()
                        val latlng : LatLng = LatLng(lat,lng)

                        if(it.id_kategori=="Pantai"){

                            markerOption.position(latlng)
                            markerOption.title(it.name_destination)
                            markerOption.snippet(it.address_destination)
                            markerOption.icon(BitmapDescriptorFactory.fromBitmap(beachMarker))
                        }else if(it.id_kategori=="Bukit"){
                            markerOption.position(latlng)
                            markerOption.title(it.name_destination)
                            markerOption.snippet(it.address_destination)
                            markerOption.icon(BitmapDescriptorFactory.fromBitmap(hillMarker))

                        }else if(it.id_kategori=="Gunung"){
                            markerOption.position(latlng)
                            markerOption.title(it.name_destination)
                            markerOption.snippet(it.address_destination)
                            markerOption.icon(BitmapDescriptorFactory.fromBitmap(mountainMarker))

                        }else if(it.id_kategori=="Air Terjun"){
                            markerOption.position(latlng)
                            markerOption.title(it.name_destination)
                            markerOption.snippet(it.address_destination)
                            markerOption.icon(BitmapDescriptorFactory.fromBitmap(waterfallMarker))
                        }
                        val desti = Destination(id,it.name_destination, it.lat_destination,it.lng_destination, it.address_destination, it.desc_destination, it.id_kategori, it.img_destination, it.id_kabupaten, it.id_kecamatan)
                        val m = googleMap.addMarker(markerOption)
                        m.tag = desti

                        googleMap.setOnInfoWindowClickListener(object : GoogleMap.OnInfoWindowClickListener{
                            override fun onInfoWindowClick(p0: Marker?) {

                               val desti : Destination = p0!!.tag as Destination

                                val intent = Intent(context, DestinationDetailActivity::class.java)
                                intent.putExtra(DestinationDetailActivity.EXTRA_DETAIl, desti)
                                startActivity(intent)

//                                val alertDialog = AlertDialog.Builder(requireContext())
//                                val inflater = layoutInflater
//                                val convertView = inflater.inflate(R.layout.dialog_item, null)
//                                alertDialog.setView(convertView)
//                                convertView.tv_title.text = desti.name_destination
//                                convertView.tv_addres.text = desti.address_destination
//                                convertView.tv_category.text = desti.id_kategori
//                                convertView.tv_desc.text = desti.desc_destination
//
//                                alertDialog.show()
                            }
                        })
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, destination)
                        search_view.threshold=0
                        search_view.setAdapter(adapter)
                    }

                    search_view.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->

                        val a = search_view.adapter.getItem(position) as Destination
                        val b = a.lat_destination!!.toDouble()
                        val c = a.lng_destination!!.toDouble()


                                val cameraPosition = CameraPosition.Builder()
                                    .target(LatLng(b,c))
                                    .zoom(15f)
                                    .build()

                                Toast.makeText(requireContext(), " $c , $b", Toast.LENGTH_LONG).show()
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                        }
                        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    Log.d("onResponse", response.toString())
                }

                else{
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(context, "Gagal mengambil spinner", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun loadKategoriMenu(){

        filter_cat.setOnClickListener {

            val view = layoutInflater.inflate(R.layout.bottom_sheet_category, null)

            dialog = BottomSheetDialog(requireContext())
            dialog.setContentView(view)
            dialog.show()

            kategoriAdapter = KategoriAdapter(kategori,requireContext(), onKategoriClicked())
            kategoriAdapter.notifyDataSetChanged()

            mainViewModelKategori = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
                MainViewModelKategori::class.java
            )

            mainViewModelKategori.setKategori()
            view.recycle_category.layoutManager = LinearLayoutManager(requireContext())
            view.recycle_category.adapter = kategoriAdapter

            mainViewModelKategori.getKategori().observe(this, androidx.lifecycle.Observer { kategori->
                if(kategori!=null){
                    kategoriAdapter.setData(kategori)
                }
            })
        }
        }

    private fun onKategoriClicked(): (Int) -> Unit {
        return {
            val simpanKategori = kategoriAdapter.getItem(it).name_kategori
            googleMap.clear()
            filterCategory(simpanKategori)
            dialog.dismiss()
        }
    }

    private fun filterCategory(simpanKategori: String) {
        var markerOption = MarkerOptions()
        val beachBitmap = BitmapFactory.decodeResource(resources, R.drawable.beach)
        val beachMarker = Bitmap.createScaledBitmap(beachBitmap, 90, 90, false)
        val hillBitmap = BitmapFactory.decodeResource(resources, R.drawable.mountain)
        val hillMarker = Bitmap.createScaledBitmap(hillBitmap, 90, 90, false)
        val mountainBitmap = BitmapFactory.decodeResource(resources, R.drawable.mountain)
        val mountainMarker = Bitmap.createScaledBitmap(mountainBitmap, 90, 90, false)
        val waterfallBitmap = BitmapFactory.decodeResource(resources, R.drawable.waterfall)
        val waterfallMarker = Bitmap.createScaledBitmap(waterfallBitmap, 90, 90, false)
        apiService.getFilterKategori(simpanKategori).enqueue(object : Callback<DestinationResponse> {
            override fun onFailure(call: Call<DestinationResponse>, t: Throwable) {
                Toast.makeText(context, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<DestinationResponse>,
                response: Response<DestinationResponse>
            ) {
                if(response.isSuccessful){
                    destination = response.body()!!.data
                    getCurrentLocation()

                    destination.forEach {
                        val lat = it.lat_destination!!.toDouble()
                        val lng = it.lng_destination!!.toDouble()

                        val latlng : LatLng = LatLng(lat,lng)

                        if(it.id_kategori=="Pantai"){

                            markerOption.position(latlng)
                            markerOption.title(it.name_destination)
                            markerOption.snippet(it.address_destination)
                            markerOption.icon(BitmapDescriptorFactory.fromBitmap(beachMarker))
                        }else if(it.id_kategori=="Bukit"){
                            markerOption.position(latlng)
                            markerOption.title(it.name_destination)
                            markerOption.snippet(it.address_destination)
                            markerOption.icon(BitmapDescriptorFactory.fromBitmap(hillMarker))

                        }else if(it.id_kategori=="Gunung"){
                            markerOption.position(latlng)
                            markerOption.title(it.name_destination)
                            markerOption.snippet(it.address_destination)
                            markerOption.icon(BitmapDescriptorFactory.fromBitmap(mountainMarker))

                        }else if(it.id_kategori=="Air Terjun"){
                            markerOption.position(latlng)
                            markerOption.title(it.name_destination)
                            markerOption.snippet(it.address_destination)
                            markerOption.icon(BitmapDescriptorFactory.fromBitmap(waterfallMarker))
                        }
                        val desti = Destination(id,it.name_destination, it.lat_destination,it.lng_destination, it.address_destination, it.desc_destination, it.id_kategori, it.img_destination, it.id_kabupaten, it.id_kecamatan)
                        val m = googleMap.addMarker(markerOption)
                        m.tag = desti

                        googleMap.setOnInfoWindowClickListener(object : GoogleMap.OnInfoWindowClickListener{
                            override fun onInfoWindowClick(p0: Marker?) {

                                val desti : Destination = p0!!.tag as Destination

                                val intent = Intent(context, DestinationDetailActivity::class.java)
                                intent.putExtra(DestinationDetailActivity.EXTRA_DETAIl, desti)
                                startActivity(intent)

//                                val alertDialog = AlertDialog.Builder(requireContext())
//                                val inflater = layoutInflater
//                                val convertView = inflater.inflate(R.layout.dialog_item, null)
//                                alertDialog.setView(convertView)
//                                convertView.tv_title.text = desti.name_destination
//                                convertView.tv_addres.text = desti.address_destination
//                                convertView.tv_category.text = desti.id_kategori
//                                convertView.tv_desc.text = desti.desc_destination
//
//                                alertDialog.show()
                            }
                        })

                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, destination)

                        search_view.threshold=0
                        search_view.setAdapter(adapter)
                    }

                    search_view.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->

                        val a = search_view.adapter.getItem(position) as Destination
                        val b = a.lat_destination!!.toDouble()
                        val c = a.lng_destination!!.toDouble()


                        val cameraPosition = CameraPosition.Builder()
                            .target(LatLng(b,c))
                            .zoom(15f)
                            .build()

                        Toast.makeText(requireContext(), " $c , $b", Toast.LENGTH_LONG).show()

                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                    }
                    googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE


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
