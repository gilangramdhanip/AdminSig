package com.emoji.adminsig.activities

import SessionManager
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.PermissionRequest
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emoji.adminsig.R
import com.emoji.adminsig.helpers.DestinationAdapter
import com.emoji.adminsig.helpers.PencarianAdapter
import com.emoji.adminsig.models.*
import com.emoji.adminsig.services.ServiceBuilder
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_destiny_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DestinationListActivity : AppCompatActivity(), PermissionListener {

    private val apiService = ServiceBuilder.create()

    private lateinit var destinationAdapter: DestinationAdapter
    lateinit var pencarianAdapter: PencarianAdapter
    private lateinit var mainViewModel: MainViewModel
    private val destination = ArrayList<Destination>()

    private lateinit var kabupaten : String
    private lateinit var kecamatan : String

    lateinit var simpanNamaKab : String
    lateinit var simpanNamaKec : String

    private val des :Destination? = null
    private lateinit var spinner: Spinner
    private lateinit var spinnerKab : Array<Kabupaten>
    private lateinit var spinnerKec : Array<Kecamatan>
    private lateinit var sessionManager: SessionManager

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    companion object {
        const val REQUEST_CHECK_SETTINGS = 43
    }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_destiny_list)

        val id = sessionManager.getId()

        Toast.makeText(baseContext, "$id", Toast.LENGTH_SHORT).show()

		setSupportActionBar(toolbar)
		toolbar.title = title

        fusedLocationProviderClient = FusedLocationProviderClient(application)

        if (isPermissionGiven()){
            getCurrentLocation()
        } else {
            givePermission()
        }

        spin_kabupaten.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{


            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if(position == 0){
                    spin_kecamatan.visibility = View.GONE
                    destiny_recycler_view.clearOnChildAttachStateChangeListeners()
                    loadDestination()
                }else if(position > 0){
                    spin_kecamatan.visibility = View.VISIBLE
                    kabupaten = spin_kabupaten.selectedItem.toString()
                    destinationAdapter.filter.filter(kabupaten)
                    simpanNamaKab = spinnerKab[position-1].id_kabupaten
                    setKecamatanSpinner(simpanNamaKab)
                }
            }

        }

        spin_kecamatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if(position == 0){
                    kecamatan = ""
                    destinationAdapter.filter.filter(kabupaten)
                    setKecamatan(kabupaten, kecamatan)
                }else{
                    kecamatan = spin_kecamatan.selectedItem.toString()
                    destinationAdapter.filter.filter(kecamatan)
                    setKecamatan(kabupaten, kecamatan)
                }
                Toast.makeText(this@DestinationListActivity, " Kamu memilih spinner "+kecamatan, Toast.LENGTH_SHORT).show()
            }

        }

		fab.setOnClickListener {
			val intent = Intent(this@DestinationListActivity, MapsTambah::class.java)
			startActivity(intent)
		}

    }

    private fun isPermissionGiven(): Boolean{
        return ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun givePermission() {
        Dexter.withActivity(this@DestinationListActivity)
            .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(this)
            .check()
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        getCurrentLocation()
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: com.karumi.dexter.listener.PermissionRequest?,
        token: PermissionToken?
    ) {
        TODO("Not yet implemented")
    }

    fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest?,
        token: PermissionToken?
    ) {
        token!!.continuePermissionRequest()
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Toast.makeText(this@DestinationListActivity, "Permission required for showing location", Toast.LENGTH_LONG).show()
        this?.finish()
    }

    private fun getCurrentLocation() {

        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        val result = LocationServices.getSettingsClient(application).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent){

                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this@DestinationListActivity,
                            MapsTambah.REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.keluar) {
            // Logout
            logout()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun showLoading(state: Boolean){
        if(state){
            progressbar.visibility= View.VISIBLE
        }
        else{
            progressbar.visibility= View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        val imm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        loadDestination()
        initSpinnerKabupaten()
    }


    private fun loadDestination(){

        destinationAdapter = DestinationAdapter(destination)
        destinationAdapter.notifyDataSetChanged()

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainViewModel::class.java
        )

        showLoading(true)

        mainViewModel.setDestination()
        destiny_recycler_view.setHasFixedSize(true)
        destiny_recycler_view.layoutManager = LinearLayoutManager(applicationContext)
        destiny_recycler_view.adapter = destinationAdapter

        mainViewModel.getDestination().observe(this, Observer { destination ->
            if(destination!=null){
                destinationAdapter.setData(destination)
                txv_jumlah_destinasi.text = destinationAdapter.itemCount.toString()

                    btn_cari.setOnClickListener {
                        search_view.visibility = View.VISIBLE
                        btn_cari.visibility = View.GONE
                        bar.visibility = View.GONE
                        fab.visibility = View.GONE

                        val adapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, destination)
                        search_view.threshold=0
                        search_view.setAdapter(adapter)
                        search_view.setOnItemClickListener { adapterView, view, i, l ->
                            val a = search_view.adapter.getItem(i) as Destination
                            destinationAdapter.setFlter(a)
                            txv_jumlah_destinasi.text = destinationAdapter.itemCount.toString()

                            if(search_view.text.equals("")){
                                btn_clear.visibility = View.GONE
                            }else{
                                btn_clear.visibility = View.VISIBLE
                                btn_clear.setOnClickListener {
                                    search_view.text.clear()
                                    btn_cari.visibility = View.VISIBLE
                                    search_view.visibility = View.GONE
                                    btn_clear.visibility = View.GONE
                                    bar.visibility = View.VISIBLE
                                    fab.visibility = View.VISIBLE
                                    val imm: InputMethodManager =
                                        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                                    initSpinnerKabupaten()
                                }
                            }
                    }
                }


                showLoading(false)
            }
        })
    }

    private fun initSpinnerKabupaten(){

        apiService.getKabupaten().enqueue(object : Callback<KabupatenResponse>{
            override fun onFailure(call: Call<KabupatenResponse>, t: Throwable) {
                Toast.makeText(this@DestinationListActivity, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<KabupatenResponse>,
                response: Response<KabupatenResponse>
            ) {
                if(response.isSuccessful){
                    spinnerKab = response.body()!!.data
                    val listSpinner = ArrayList<String>(spinnerKab.size)
                    listSpinner.add("Pilih Kabupaten")
                    spinnerKab.forEach {
                        listSpinner.add(it.name_kabupaten)
                    }

                    val adapter = ArrayAdapter(this@DestinationListActivity, android.R.layout.simple_spinner_item, listSpinner)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spin_kabupaten.adapter = adapter
                    txv_jumlah_destinasi.text = destinationAdapter.itemCount.toString()
                    Log.d("KabupatenResponse", response.toString())
                }

                else{
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(this@DestinationListActivity, "Gagal mengambil spinner", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }


    private fun setKecamatanSpinner(idkabupaten: String){
        apiService.getKecamatan(idkabupaten).enqueue(object : Callback<KecamatanResponse>{
            override fun onFailure(call: Call<KecamatanResponse>, t: Throwable) {
                Toast.makeText(this@DestinationListActivity, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<KecamatanResponse>,
                response: Response<KecamatanResponse>
            ) {
                if(response.isSuccessful){
                    spinnerKec = response.body()!!.data
                    val listSpinner = ArrayList<String>(spinnerKec.size)
                    listSpinner.add("Pilih Kecamatan")
                    spinnerKec.forEach {
                        listSpinner.add(it.name_kecamatan)
                    }

                    val adapter = ArrayAdapter(this@DestinationListActivity, android.R.layout.simple_spinner_item, listSpinner)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spin_kecamatan.adapter = adapter
                    txv_jumlah_destinasi.text = destinationAdapter.itemCount.toString()
                    Log.d("berhasilResponse", response.toString())
                }

                else{
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(this@DestinationListActivity, "Gagal mengambil spinner", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun setKecamatan(idkabupaten: String,idkecamatan: String){
        apiService.getKecamatanbyid(idkabupaten,idkecamatan).enqueue(object : Callback<KecamatanResponse>{
            override fun onFailure(call: Call<KecamatanResponse>, t: Throwable) {
                Toast.makeText(this@DestinationListActivity, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<KecamatanResponse>,
                response: Response<KecamatanResponse>
            ) {
                if(response.isSuccessful){
                    txv_jumlah_destinasi.text = destinationAdapter.itemCount.toString()
                    Log.d("berhasilResponse", response.toString())
                }

                else{
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(this@DestinationListActivity, "Gagal mengambil spinner", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    fun logout() {
        sessionManager.clearSession()
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}









