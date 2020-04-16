package com.emoji.adminsig.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.emoji.adminsig.R
import com.emoji.adminsig.models.*
import com.emoji.adminsig.services.ServiceBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.skripsi.sigwam.model.Kategori
import com.skripsi.sigwam.model.KategoriResponse
import com.vincent.filepicker.Constant.*
import com.vincent.filepicker.activity.ImagePickActivity
import com.vincent.filepicker.filter.entity.ImageFile
import kotlinx.android.synthetic.main.activity_destiny_create.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class DestinationCreateActivity : AppCompatActivity() {

    private lateinit var simpan: String
    private lateinit var kabupaten: String
    private lateinit var kecamatan: String

    lateinit var simpanNamaKab: String
    lateinit var filepath : Uri
    lateinit var bitmap: Bitmap
    lateinit var path: String
    private lateinit var spinnerCat: Array<Kategori>
    private lateinit var spinnerKab: Array<Kabupaten>
    private lateinit var spinnerKec: Array<Kecamatan>

    private val apiService = ServiceBuilder.create()
    lateinit var img_destination: MultipartBody.Part

    val RequestPermissionCode = 1
    var mLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destiny_create)

        initSpinnerKabupaten()
        initSpinnerCategory()
//        requestPermissionStorage()

        ib_img.setOnClickListener {
            if(EasyPermissions.hasPermissions(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
//                val i = Intent(this, ImagePickActivity::class.java)
//                i.putExtra(MAX_NUMBER,1)
//                startActivityForResult(i, REQUEST_CODE_PICK_IMAGE)
                val i = Intent(this, ImagePickActivity::class.java)
                i.putExtra(MAX_NUMBER,1)
                startActivityForResult(i, REQUEST_CODE_PICK_IMAGE)
            }else{
                // tampilkan permission request saat belum mendapat permission dari user
                EasyPermissions.requestPermissions(this,"This application need your permission to access photo gallery.",991,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        et_kabupaten.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (position == 0) {
                    et_kecamatan.visibility = View.GONE
                    kecamatan = ""
                    kabupaten = ""
                } else {
                    et_kecamatan.visibility = View.VISIBLE
                    kabupaten = et_kabupaten.selectedItem.toString()
                    simpanNamaKab = spinnerKab[position - 1].id_kabupaten
                    setKecamatanSpinner(simpanNamaKab)
                }
//                Toast.makeText(this@DestinationCreateActivity, " Kamu memilih spinner "+kabupaten, Toast.LENGTH_SHORT).show()
            }

        }

        et_kecamatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent!!.selectedItem == "Pilih Kecamatan") {
                    kecamatan = ""
                } else {
                    kecamatan = et_kecamatan.selectedItem.toString()
                }

//                simpanNamaKec = spinnerKec[position].name_kecamatan
//                Toast.makeText(this@DestinationCreateActivity, " Kamu memilih spinner "+kecamatan, Toast.LENGTH_SHORT).show()
            }

        }

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        tambahdata()

        et_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (parent!!.selectedItem == "Pilih Kategori") {
                    simpan = ""
                } else {
                    simpan = et_category.selectedItem.toString()
                }
//                Toast.makeText(this@DestinationCreateActivity, " Kamu memilih spinner "+kecamatan, Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun initSpinnerKabupaten() {

        apiService.getKabupaten().enqueue(object : Callback<KabupatenResponse> {
            override fun onFailure(call: Call<KabupatenResponse>, t: Throwable) {
                Toast.makeText(
                    this@DestinationCreateActivity,
                    "Koneksi internet bermasalah",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<KabupatenResponse>,
                response: Response<KabupatenResponse>
            ) {
                if (response.isSuccessful) {
                    spinnerKab = response.body()!!.data
                    val listSpinner = ArrayList<String>(spinnerKab.size)

                    listSpinner.add("Pilih Kabupaten")

                    spinnerKab.forEach {
                        listSpinner.add(it.name_kabupaten)
                    }

                    val adapter = ArrayAdapter(
                        this@DestinationCreateActivity,
                        android.R.layout.simple_spinner_item,
                        listSpinner
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    et_kabupaten.adapter = adapter
                    Log.d("KabupatenResponse", response.toString())
                } else {
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(
                        this@DestinationCreateActivity,
                        "Gagal mengambil spinner",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })
    }


    private fun setKecamatanSpinner(idkabupaten: String) {
        apiService.getKecamatan(idkabupaten).enqueue(object : Callback<KecamatanResponse> {
            override fun onFailure(call: Call<KecamatanResponse>, t: Throwable) {
                Toast.makeText(
                    this@DestinationCreateActivity,
                    "Koneksi internet bermasalah",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<KecamatanResponse>,
                response: Response<KecamatanResponse>
            ) {
                if (response.isSuccessful) {
                    spinnerKec = response.body()!!.data
                    val listSpinner = ArrayList<String>(spinnerKec.size)

                    listSpinner.add("Pilih Kecamatan")

                    spinnerKec.forEach {
                        listSpinner.add(it.name_kecamatan)
                    }

                    val adapter = ArrayAdapter(
                        this@DestinationCreateActivity,
                        android.R.layout.simple_spinner_item,
                        listSpinner
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    et_kecamatan.adapter = adapter
                    Log.d("berhasilResponse", response.toString())
                } else {
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(
                        this@DestinationCreateActivity,
                        "Gagal mengambil spinner",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })
    }


    private fun initSpinnerCategory() {

        apiService.getKategori().enqueue(object : Callback<KategoriResponse> {
            override fun onFailure(call: Call<KategoriResponse>, t: Throwable) {
                Toast.makeText(
                    this@DestinationCreateActivity,
                    "Koneksi internet bermasalah",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<KategoriResponse>,
                response: Response<KategoriResponse>
            ) {
                if (response.isSuccessful) {
                    spinnerCat = response.body()!!.data
                    val listSpinner = ArrayList<String>(spinnerCat.size)
                    listSpinner.add("Pilih Kategori")

                    spinnerCat.forEach {
                        listSpinner.add(it.name_kategori)
                    }

                    val adapter = ArrayAdapter(
                        this@DestinationCreateActivity,
                        android.R.layout.simple_spinner_item,
                        listSpinner
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    et_category.adapter = adapter
                    Log.d("KabupatenResponse", response.toString())
                } else {
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(
                        this@DestinationCreateActivity,
                        "Gagal mengambil spinner",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })
    }

fun createPartFromString(descriptionString : String) : RequestBody {
    return RequestBody.create(
            okhttp3.MultipartBody.FORM, descriptionString);
}

    private fun tambahdata() {

        btn_add.setOnClickListener {

            val map = HashMap<String, RequestBody>()
            map["name_destination"] = createPartFromString(et_name.text.toString())
            map["lat_destination"] = createPartFromString(et_latitude.text.toString())
            map["lng_destination"] = createPartFromString(et_longitude.text.toString())
            map["address_destination"] = createPartFromString(et_address.text.toString())
            map["desc_destination"] = createPartFromString(et_description.text.toString())
            map["id_kategori"] = createPartFromString(simpan)
            map["id_kabupaten"] = createPartFromString(kabupaten)
            map["id_kecamatan"] = createPartFromString(kecamatan)

//            val newDestination = Destination(taskId)
//            newDestination.name_destination = et_name.text.toString()
//            newDestination.lat_destination = et_latitude.text.toString()
//            newDestination.lng_destination = et_longitude.text.toString()
//            newDestination.address_destination = et_address.text.toString()
//            newDestination.desc_destination = et_description.text.toString()
//            newDestination.id_kategori = simpan
//            newDestination.img_destination = img_destination.toString()
//            newDestination.id_kabupaten = kabupaten
//            newDestination.id_kecamatan = kecamatan




            val destinationService = ServiceBuilder.create()
            val requestCall = destinationService.addDestination(map, img_destination)

            requestCall.enqueue(object : Callback<DestinationResponse> {

                override fun onResponse(
                    call: Call<DestinationResponse>,
                    response: Response<DestinationResponse>
                ) {
                    if (response.isSuccessful) {
                        finish() // Move back to DestinationListActivity
                        var newlyCreatedDestination = response.body() // Use it or ignore it
                        Toast.makeText(
                            this@DestinationCreateActivity,
                            "Successfully Added",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("ResponseLog", response.toString())
                    } else {
                        Toast.makeText(
                            this@DestinationCreateActivity,
                            "Gagal di tambahkan",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("gagalLog", response.toString())
                    }
                }

                override fun onFailure(call: Call<DestinationResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Failed to add item", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("FailureLog", t.message)
                }
            })
        }
    }

    fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->

                    val gcd = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address>
                    addresses = gcd.getFromLocation(location!!.latitude, location.longitude, 1)
                    mLocation = location
                    if (location != null) {
                        et_latitude.text =
                            Editable.Factory.getInstance().newEditable(location.latitude.toString())
                        et_longitude.text = Editable.Factory.getInstance()
                            .newEditable(location.longitude.toString())
                        et_address.text = Editable.Factory.getInstance()
                            .newEditable(addresses[0].getAddressLine(0))
                    }
                }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            RequestPermissionCode
        )
        this.recreate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null){

            // menghiden button btnPickUpload.
//            ib_img.visibility = View.GONE

            // memunculkan btnUpload dan imgUpload.
            iv_image.visibility = View.VISIBLE

            // membuat variable yang menampung path dari picked image.
            val pickedImg = data.getParcelableArrayListExtra<ImageFile>(RESULT_PICK_IMAGE)[0]?.path

            // membuat request body yang berisi file dari picked image.
            val requestBody = RequestBody.create(MediaType.parse("multipart"), File(pickedImg))

            // mengoper value dari requestbody sekaligus membuat form data untuk upload. dan juga mengambil nama dari picked image
            img_destination = MultipartBody.Part.createFormData("img_destination",File(pickedImg)?.name,requestBody)

            // mempilkan image yang akan diupload dengan glide ke imgUpload.
            Glide.with(this).load(pickedImg).into(iv_image)

        }
    }

}

