package com.skripsi.sigwam.activity

import com.emoji.adminsig.preferencetools.SessionManager
import android.Manifest
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.location.Address
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
import androidx.appcompat.widget.Toolbar
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.skripsi.sigwam.MainActivity
import com.skripsi.sigwam.R
import com.skripsi.sigwam.model.*
import com.skripsi.sigwam.service.ServiceBuilder
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_destiny_create.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class DestinationCreateActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CREATE = "extra_create"
    }

    private lateinit var simpan: String
    private lateinit var kabupaten: String
    private lateinit var kecamatan: String

    lateinit var simpanNamaKab: String
    var pickedImg: String? = null
    lateinit var bitmap: Bitmap
    lateinit var path: String
    private lateinit var spinnerCat: Array<Kategori>
    private lateinit var spinnerKab: Array<Kabupaten>
    private lateinit var spinnerKec: Array<Kecamatan>

    private val apiService = ServiceBuilder.create()
    var img_destination: MultipartBody.Part? = null
    var REQUEST_CODE = 100

    val RequestPermissionCode = 1
    var mLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var destination : Address
    private lateinit var lat : String
    private lateinit var lng : String
    val PICK_IMAGE_REQUEST = 1
    private lateinit var sessionManager: SessionManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destiny_create)
        sessionManager = SessionManager(this)
        val id = sessionManager.getId()

        val toolbar: Toolbar? = findViewById<Toolbar>(R.id.profileToolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })
        destination = intent.getParcelableExtra(EXTRA_CREATE) as Address
        et_latitude.text =Editable.Factory.getInstance().newEditable(destination.latitude.toString())
        et_longitude.text = Editable.Factory.getInstance().newEditable(destination.longitude.toString())
        et_address.text = Editable.Factory.getInstance().newEditable(destination.getAddressLine(0).toString())


        initSpinnerKabupaten()
        initSpinnerCategory()
//        requestPermissionStorage()
        ib_img.setOnClickListener {
            if(EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                try{
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
                }catch(e: ActivityNotFoundException){
                    e.printStackTrace()
                }
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
//        getLastLocation()

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


        et_jambuka.setOnClickListener {
            val calendar = Calendar.getInstance()
            val jam = calendar.get(Calendar.HOUR_OF_DAY)
            val menit = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                OnTimeSetListener { view, hourOfDay, minute -> et_jambuka.setText("$hourOfDay:$minute") },
                jam,
                menit,
                false
            )
            timePickerDialog.show()
        }

        et_jamtutup.setOnClickListener {
            val calendar = Calendar.getInstance()
            val jam = calendar.get(Calendar.HOUR_OF_DAY)
            val menit = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                OnTimeSetListener { view, hourOfDay, minute -> et_jamtutup.setText("$hourOfDay:$minute") },
                jam,
                menit,
                false
            )
            timePickerDialog.show()
        }

                    btn_add.setOnClickListener {

                        MaterialAlertDialogBuilder(this)
                            .setTitle("Alert!")
                            .setMessage("Apakah Data Sudah benar?")
                            .setPositiveButton("Ya",
                                DialogInterface.OnClickListener { dialog, which ->

                                    Snackbar.make(it, "Harap Tunggu Sebentar", Snackbar.LENGTH_LONG).show()
                        val map = HashMap<String, RequestBody>()
                        map["name_destination"] = createPartFromString(et_name.text.toString())
                        map["lat_destination"] = createPartFromString(et_latitude.text.toString())
                        map["lng_destination"] = createPartFromString(et_longitude.text.toString())
                        map["address_destination"] = createPartFromString(et_address.text.toString())
                        map["desc_destination"] = createPartFromString(et_description.text.toString())
                        map["id_kategori"] = createPartFromString(simpan)
                        map["id_kabupaten"] = createPartFromString(kabupaten)
                        map["id_kecamatan"] = createPartFromString(kecamatan)
                        map["jambuka"] = createPartFromString(et_jambuka.text.toString())
                        map["jamtutup"] = createPartFromString(et_jamtutup.text.toString())
                        map["id_admin"] = createPartFromString("")
                        map["status"] = createPartFromString("0")
                        map["id_wisatawan"] = createPartFromString(sessionManager.getId())

                        val destinationService = ServiceBuilder.create()
                        val requestCall = destinationService.addDestination(map, img_destination)

                        requestCall.enqueue(object : Callback<DestinationResponse> {

                            override fun onResponse(
                                call: Call<DestinationResponse>,
                                response: Response<DestinationResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val intent = Intent(this@DestinationCreateActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    Snackbar.make(it, "Data Berhasil ditambahkan", Snackbar.LENGTH_LONG).show()
                                    finishAffinity()
                                    var newlyCreatedDestination = response.body() // Use it or ignore it
                                    Log.d("ResponseLog", response.toString())
                                } else {
                                    Snackbar.make(it, "Data Gagal ditambahkan", Snackbar.LENGTH_LONG).show()
                                    Log.d("gagalLog", response.toString())
                                }
                            }

                            override fun onFailure(call: Call<DestinationResponse>, t: Throwable) {
                                Snackbar.make(it, "Data Gagal ditambahkan", Snackbar.LENGTH_LONG).show()
                                Log.d("FailureLog", t.message)
                            }
                        })

                                })
                            .setNegativeButton("Kembali",null)
                            .setIcon(R.drawable.personmarker)
                            .show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK){

            if(requestCode == PICK_IMAGE_REQUEST){
                val picUri: Uri? = data!!.data
                iv_image.visibility = View.VISIBLE
                // membuat variable yang menampung path dari picked image.
                        pickedImg = picUri?.let { getPath(it) }

                val compressedImageFile = Compressor(this).compressToFile(File(pickedImg))

                    Toast.makeText(applicationContext, "$pickedImg", Toast.LENGTH_SHORT).show()
                    // membuat request body yang berisi file dari picked image.
                if(!pickedImg.isNullOrEmpty()){
                    val requestBody = RequestBody.create("multipart".toMediaTypeOrNull(), compressedImageFile)
                    img_destination = MultipartBody.Part.createFormData("img_destination",
                        compressedImageFile.name,requestBody)
                    Toast.makeText(baseContext, "$compressedImageFile", Toast.LENGTH_SHORT ).show()
                    Glide.with(this).load(pickedImg).into(iv_image)
                }else{
                    val requestBody = RequestBody.create(MultipartBody.FORM, "")
                    img_destination = MultipartBody.Part.createFormData("img_destination", "",requestBody)
                }
            }
        }
    }

    private fun getPath(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader =
            CursorLoader(applicationContext, contentUri, proj, null, null, null)
        val cursor: Cursor = loader.loadInBackground()!!
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result: String = cursor.getString(column_index)
        cursor.close()
        return result
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Alert!")
            .setMessage("Yakin ingin keluar dari halaman ini? Jika anda keluar, data akan terhapus!")
            .setPositiveButton("Ya",
                DialogInterface.OnClickListener { dialog, which ->
                    super.onBackPressed()
                    finish()
                })
            .setNegativeButton("Kembali",null)
            .setIcon(R.drawable.helpicon)
            .show()
    }

}

