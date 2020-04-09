package com.emoji.adminsig.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.emoji.adminsig.R
import com.emoji.adminsig.models.*
import com.emoji.adminsig.services.ServiceBuilder
import com.skripsi.sigwam.model.Kategori
import com.skripsi.sigwam.model.KategoriResponse
import kotlinx.android.synthetic.main.activity_destiny_create.*
import kotlinx.android.synthetic.main.activity_destiny_detail.*
import kotlinx.android.synthetic.main.activity_destiny_detail.et_address
import kotlinx.android.synthetic.main.activity_destiny_detail.et_description
import kotlinx.android.synthetic.main.activity_destiny_detail.et_image
import kotlinx.android.synthetic.main.activity_destiny_detail.et_latitude
import kotlinx.android.synthetic.main.activity_destiny_detail.et_longitude
import kotlinx.android.synthetic.main.activity_destiny_detail.et_name
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class DestinationDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DETAIl = "extra_detail"
    }

    lateinit var simpan : String
    lateinit var kabupaten : String
    lateinit var kecamatan : String
    lateinit var simpanNamaKab : String

    lateinit var spinnerKab: Array<Kabupaten>
    lateinit var spinnerKec : Array<Kecamatan>
    lateinit var spinnerCat : Array<Kategori>
    private var ambil :Int = 0

    private lateinit var destination : Destination
    private val apiService = ServiceBuilder.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destiny_detail)

        destination = intent.getParcelableExtra(EXTRA_DETAIl) as Destination

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

        destination.id_destination
        et_name.setText(destination.name_destination)
        et_latitude.setText(destination.lat_destination)
        et_longitude.setText(destination.lng_destination)
        et_address.setText(destination.address_destination)
        et_description.setText(destination.desc_destination)
        et_image.setText(destination.img_destination)


//        setSupportActionBar(detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_delete.setOnClickListener{

                    val destinationService = ServiceBuilder.create()
                    val requestCall = destinationService.deleteDestination(destination.id_destination)

                    requestCall.enqueue(object: Callback<DeleteResponse> {

                        override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                            if (response.isSuccessful) {
                                finish() // Move back to DestinationListActivity
                                Toast.makeText(this@DestinationDetailActivity, "Successfully Deleted", Toast.LENGTH_SHORT).show()
                                Log.d("dihapus", response.toString())
                            } else {
                                Toast.makeText(this@DestinationDetailActivity, "hayyooo to Delete", Toast.LENGTH_SHORT).show()
                                Log.d("FailureLog", response.toString())
                            }
                        }

                        override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                            Toast.makeText(this@DestinationDetailActivity, "Failed to Delete", Toast.LENGTH_SHORT).show()
                            Log.d("FailureLog", t.message)
                        }
                    })
            }


            btn_update.setOnClickListener{
                val name = et_name.text.toString()
                val lat = et_latitude.text.toString()
                val lng = et_longitude.text.toString()
                val address = et_address.text.toString()
                val description = et_description.text.toString()
                val img = et_image.text.toString()
                val cat = simpan
                val kab = kabupaten
                val kec = kecamatan


                val destinationService = ServiceBuilder.create()
                val requestCall = destinationService.updateDestination(destination.id_destination, name, lat, lng, address, description,img,cat, kab, kec )

                requestCall.enqueue(object: Callback<DestinationResponse> {

                    override fun onResponse(call: Call<DestinationResponse>, response: Response<DestinationResponse>) {
                        if (response.isSuccessful) {
                            finish() // Move back to DestinationListActivity
                            var updatedDestination = response.body() // Use it or ignore It
                            Toast.makeText(this@DestinationDetailActivity,
                                "Item Updated Successfully", Toast.LENGTH_SHORT).show()
                            Log.d("onResponse", response.toString())
                        } else {
                            Toast.makeText(this@DestinationDetailActivity,
                                "Failed to update item", Toast.LENGTH_SHORT).show()
                            Log.d("respongagal", response.toString())
                        }
                    }

                    override fun onFailure(call: Call<DestinationResponse>, t: Throwable) {
                        Toast.makeText(this@DestinationDetailActivity,
                            "Failed to update item", Toast.LENGTH_SHORT).show()
                        Log.d("respon gagagal", t.message)
                    }
                })
            }


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
                Toast.makeText(this@DestinationDetailActivity, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
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
                    val kabupatenN = spinnerKab.firstOrNull { it.name_kabupaten == destination.id_kabupaten }
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
                Toast.makeText(this@DestinationDetailActivity, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
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

                    val kecamatanN = spinnerKec.firstOrNull { it.name_kecamatan == destination.id_kecamatan }
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
                Toast.makeText(this@DestinationDetailActivity, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
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
                    val kategori = spinnerCat.firstOrNull { it.name_kategori == destination.id_kategori }
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

}
