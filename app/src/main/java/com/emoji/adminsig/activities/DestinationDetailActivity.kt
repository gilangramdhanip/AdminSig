package com.emoji.adminsig.activities

import com.emoji.adminsig.preferencetools.SessionManager
import android.Manifest
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.emoji.adminsig.R
import com.emoji.adminsig.models.*
import com.emoji.adminsig.services.ServiceBuilder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.skripsi.sigwam.model.Kategori
import com.skripsi.sigwam.model.KategoriResponse
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_destiny_detail.*
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
import kotlin.collections.set


class DestinationDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DETAIl = "extra_detail"
        const val EXTRA_BARU = "extra_baru"
    }

    lateinit var simpan : String
    lateinit var kabupaten : String
    lateinit var kecamatan : String
    lateinit var simpanNamaKab : String
    lateinit var jambuka : String
    lateinit var jamtutup : String

    lateinit var spinnerKab: Array<Kabupaten>
    lateinit var spinnerKec : Array<Kecamatan>
    lateinit var spinnerCat : Array<Kategori>
    private var ambil :Int = 0
    var pickedImg: String? = null
    private var destination : Destination? = null
    private val apiService = ServiceBuilder.create()
    var img_destination: MultipartBody.Part? = null
    val PICK_IMAGE_REQUEST = 1
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destiny_detail)
        sessionManager = SessionManager(this)
        destination = intent.getParcelableExtra(EXTRA_DETAIl) as? Destination
        val toolbar: Toolbar? = findViewById<Toolbar>(R.id.profileToolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        initSpinnerKabupaten()
        initSpinnerCategory()

        spin_kab.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                kabupaten = spin_kab.selectedItem.toString()
                simpanNamaKab = spinnerKab[position].id_kabupaten
                setKecamatanSpinner(simpanNamaKab)
                Toast.makeText(this@DestinationDetailActivity, " Kamu memilih spinner "+spinnerKab[position].name_kabupaten, Toast.LENGTH_SHORT).show()
            }

        }

        spin_kec.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                kecamatan = spin_kec.selectedItem.toString()
                //Toast.makeText(this@DestinationDetailActivity, " Kamu memilih spinner "+spinnerKec[position].name_kecamatan, Toast.LENGTH_SHORT).show()
            }

        }

        spin_cat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                simpan = spin_cat.selectedItem.toString()
                //Toast.makeText(this@DestinationDetailActivity, " Kamu memilih spinner "+spinnerKec[position].name_kecamatan, Toast.LENGTH_SHORT).show()
            }

        }


        //Load Destination

        destination?.id_destination
        et_name.setText(destination?.name_destination)
        et_latitude.setText(destination?.lat_destination)
        et_longitude.setText(destination?.lng_destination)
        et_address.setText(destination?.address_destination)
        et_description.setText(destination?.desc_destination)
        et_jambuka.setText(destination?.jambuka)
        et_jamtutup.setText(destination?.jamtutup)

        if(destination?.img_destination == ""){
            img_view.setImageResource(R.drawable.default_img)
        }else{
            Glide.with(applicationContext)
                .load("http://siwita-lombok.monster/rest_api/rest-server-sig/assets/foto/"+destination?.img_destination)
                .apply(RequestOptions().override(1280, 720))
                .into(img_view)
        }


        buka_peta.setOnClickListener {
            val intent = Intent(this@DestinationDetailActivity, MapsUpdate::class.java)
            intent.putExtra(MapsUpdate.EXTRA_UPDATE, destination)
            startActivity(intent)
        }

        btn_img.setOnClickListener {
            if(EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                // Start the Intent
                // Start the Intent
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
//                val i = Intent(this, ImagePickActivity::class.java)
//                i.putExtra(MAX_NUMBER,1)
//                startActivityForResult(i, REQUEST_CODE_PICK_IMAGE)
            }else{
                // tampilkan permission request saat belum mendapat permission dari user
                EasyPermissions.requestPermissions(this,"This application need your permission to access photo gallery.",991,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }


//        setSupportActionBar(detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_delete.setOnClickListener{

            MaterialAlertDialogBuilder(this)
                .setTitle("Alert!")
                .setMessage("Apakah anda yakin ingin menghapus data? Data yang dihapus tidak dapat di kembalikan!")
                .setPositiveButton("Yakin",
                    DialogInterface.OnClickListener { dialog, which ->
                        Snackbar.make(it, "Data Sedang dihapus", Snackbar.LENGTH_LONG).show()

                    val destinationService = ServiceBuilder.create()
                    val requestCall = destinationService.deleteDestination(destination!!.id_destination)

                    requestCall.enqueue(object: Callback<DeleteResponse> {

                        override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                            if (response.isSuccessful) {
                                val intent = Intent(this@DestinationDetailActivity, DestinationListActivity::class.java)
                                startActivity(intent)
                                Snackbar.make(it, "Data Berhasil dihapus", Snackbar.LENGTH_LONG).show()
                                finish()

                                Log.d("dihapus", response.toString())
                            } else {
                                Snackbar.make(it, "Data Gagal dihapus", Snackbar.LENGTH_LONG).show()
                                Log.d("FailureLog", response.toString())
                            }
                        }

                        override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                            Snackbar.make(it, "Data Gagal dihapus", Snackbar.LENGTH_LONG).show()
                            Log.d("FailureLog", t.message)
                        }

                    })

                    })
                .setNegativeButton("Kembali",null)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .show()
            }


            btn_update.setOnClickListener{

                MaterialAlertDialogBuilder(this)
                    .setTitle("Alert!")
                    .setMessage("Apakah anda yakin ingin mengubah data?")
                    .setPositiveButton("Yakin",
                        DialogInterface.OnClickListener { dialog, which ->
                            Snackbar.make(it, "Harap tunggu Sebentar", Snackbar.LENGTH_LONG).show()
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
                map["id_admin"] = createPartFromString(sessionManager.getId())
                map["status"] = createPartFromString("1")
                map["id_wisatawan"] = createPartFromString(destination!!.id_wisatawan.toString())

//                if(pickedImg==null){
//                    Toast.makeText(applicationContext, "Image not selected!", Toast.LENGTH_LONG).show()
//                }

                val destinationService = ServiceBuilder.create()
                val requestCall = destinationService.updateDestination(destination!!.id_destination, map, img_destination)

                requestCall.enqueue(object: Callback<DestinationResponse> {

                    override fun onResponse(call: Call<DestinationResponse>, response: Response<DestinationResponse>) {
                        if (response.isSuccessful) {
                            val intent = Intent(this@DestinationDetailActivity, DestinationListActivity::class.java)
                            startActivity(intent)
                            Snackbar.make(it, "Data Berhasil diubah", Snackbar.LENGTH_LONG).show()
                            finish()
                            var updatedDestination = response.body() // Use it or ignore It

                            Log.d("onResponse", response.toString())
                        } else {
                            Snackbar.make(it, "Data Gagal diubah", Snackbar.LENGTH_LONG).show()
                            Log.d("respongagal", response.toString())
                        }
                    }

                    override fun onFailure(call: Call<DestinationResponse>, t: Throwable) {
                        Snackbar.make(it, "Data Gagal diubah bro", Snackbar.LENGTH_LONG).show()
                        Log.d("respon gagagal", t.message)
                    }
                })

                        })
                    .setNegativeButton("Kembali",null)
                    .setIcon(R.drawable.ic_mood_black_24dp)
                    .show()
            }

        et_jambuka.setOnClickListener {
            val calendar = Calendar.getInstance()
            val jam = calendar.get(Calendar.HOUR_OF_DAY)
            val menit = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> et_jambuka.setText("$hourOfDay:$minute") },
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
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    et_jamtutup.setText(
                        "$hourOfDay:$minute"
                    )
                },
                jam,
                menit,
                false
            )
            timePickerDialog.show()
        }


    }

    fun createPartFromString(descriptionString : String) : RequestBody {
        return RequestBody.create(
            okhttp3.MultipartBody.FORM, descriptionString)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK){

            if(requestCode == PICK_IMAGE_REQUEST){
                val picUri: Uri? = data!!.data
                img_view.visibility = View.VISIBLE

                // membuat variable yang menampung path dari picked image.
                pickedImg = picUri?.let { getPath(it) }

                val compressedImageFile = Compressor(this).compressToFile(File(pickedImg))

                Toast.makeText(applicationContext, "$pickedImg", Toast.LENGTH_SHORT).show()

                if(!pickedImg.isNullOrEmpty()){
                    val requestBody = RequestBody.create("multipart".toMediaTypeOrNull(), compressedImageFile)
                    img_destination = MultipartBody.Part.createFormData("img_destination", compressedImageFile.name,requestBody)
                    Glide.with(this).load(pickedImg).into(img_view)
                }else{
                    val requestBody = RequestBody.create(MultipartBody.FORM, "")
                    img_destination = MultipartBody.Part.createFormData("", "",requestBody)
                }


                // mempilkan image yang akan diupload dengan glide ke imgUpload.
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            navigateUpTo(Intent(this, DestinationListActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }



    private fun initSpinnerKabupaten(){

        apiService.getKabupaten().enqueue(object : Callback<KabupatenResponse>{
            override fun onFailure(call: Call<KabupatenResponse>, t: Throwable) {
                Toast.makeText(this@DestinationDetailActivity, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<KabupatenResponse>,
                response: Response<KabupatenResponse>
            ) {
                if(response.isSuccessful){
                    spinnerKab = response.body()!!.data
                    val listSpinner = ArrayList<String>(spinnerKab.size)
                    spinnerKab.forEach {
                        listSpinner.add(it.name_kabupaten)
                    }

                    val adapter = ArrayAdapter(this@DestinationDetailActivity, android.R.layout.simple_spinner_item, listSpinner)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spin_kab.adapter = adapter
                    val kabupatenN = spinnerKab.firstOrNull { it.name_kabupaten == destination?.id_kabupaten }
                    spin_kab.setSelection(spinnerKab.indexOf(kabupatenN))
                    Log.d("KabupatenResponse", response.toString())
                }

                else{
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(this@DestinationDetailActivity, "Gagal mengambil spinner", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }


    private fun setKecamatanSpinner(idkabupaten: String){
        apiService.getKecamatan(idkabupaten).enqueue(object : Callback<KecamatanResponse>{
            override fun onFailure(call: Call<KecamatanResponse>, t: Throwable) {
                Toast.makeText(this@DestinationDetailActivity, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<KecamatanResponse>,
                response: Response<KecamatanResponse>
            ) {
                if(response.isSuccessful){
                    spinnerKec = response.body()!!.data
                    val listSpinner = ArrayList<String>(spinnerKec.size)

                    spinnerKec.forEach {
                        listSpinner.add(it.name_kecamatan)
                    }

                    val adapter = ArrayAdapter(this@DestinationDetailActivity, android.R.layout.simple_spinner_item, listSpinner)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spin_kec.adapter = adapter

                    val kecamatanN = spinnerKec.firstOrNull { it.name_kecamatan == destination?.id_kecamatan }
                    spin_kec.setSelection(spinnerKec.indexOf(kecamatanN))
                    Log.d("berhasilResponse", response.toString())
                }

                else{
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(this@DestinationDetailActivity, "Gagal mengambil spinner", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun initSpinnerCategory(){
        apiService.getKategori().enqueue(object : Callback<KategoriResponse>{
            override fun onFailure(call: Call<KategoriResponse>, t: Throwable) {
                Toast.makeText(this@DestinationDetailActivity, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<KategoriResponse>,
                response: Response<KategoriResponse>
            ) {
                if(response.isSuccessful){
                    spinnerCat = response.body()!!.data
                    val listSpinner = ArrayList<String>(spinnerCat.size)
                    spinnerCat.forEach {
                        listSpinner.add(it.name_kategori)
                    }

                    val adapter = ArrayAdapter(this@DestinationDetailActivity, android.R.layout.simple_spinner_item, listSpinner)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spin_cat.adapter = adapter
                    val kategori = spinnerCat.firstOrNull { it.name_kategori == destination?.id_kategori }
                    spin_cat.setSelection(spinnerCat.indexOf(kategori))
                    Log.d("KabupatenResponse", response.toString())
                }

                else{
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(this@DestinationDetailActivity, "Gagal mengambil spinner", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Alert!")
            .setMessage("Yakin ingin keluar dari halaman ini? Jika anda keluar, perubahan data yang dilakukan akan terhapus!")
            .setPositiveButton("Ya",
                DialogInterface.OnClickListener { dialog, which ->
                    super.onBackPressed()
                    finish()
                })
            .setNegativeButton("Kembali",null)
            .setIcon(R.drawable.ic_warning_black_24dp)
            .show()


    }


}
