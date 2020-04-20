package com.emoji.adminsig.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emoji.adminsig.R
import com.emoji.adminsig.helpers.DestinationAdapter
import com.emoji.adminsig.models.*
import com.emoji.adminsig.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_destiny_create.*
import kotlinx.android.synthetic.main.activity_destiny_list.*
import kotlinx.android.synthetic.main.activity_destiny_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DestinationListActivity : AppCompatActivity() {

    private val apiService = ServiceBuilder.create()

    private lateinit var destinationAdapter: DestinationAdapter
    private lateinit var mainViewModel: MainViewModel
    private val destination = ArrayList<Destination>()

    private lateinit var kabupaten : String
    private lateinit var kecamatan : String

    lateinit var simpanNamaKab : String

    private val des :Destination? = null
    private lateinit var spinner: Spinner
    private lateinit var spinnerKab : Array<Kabupaten>
    private lateinit var spinnerKec : Array<Kecamatan>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_destiny_list)

		setSupportActionBar(toolbar)
		toolbar.title = title

        initSpinnerKabupaten()

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
                    kabupaten = ""
                }else{
                    spin_kecamatan.visibility = View.VISIBLE
                    kabupaten = spin_kabupaten.selectedItem.toString()
                    destinationAdapter.filter.filter(kabupaten)
                    simpanNamaKab = spinnerKab[position-1].id_kabupaten
                    setKecamatanSpinner(simpanNamaKab)
                }
                Toast.makeText(this@DestinationListActivity, " Kamu memilih spinner "+kabupaten, Toast.LENGTH_SHORT).show()
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

                if(parent!!.selectedItem == "Pilih Kecamatan"){
                    kecamatan = ""
                }else{
                    kecamatan = spin_kecamatan.selectedItem.toString()
                    destinationAdapter.filter.filter(kecamatan)
                }
                Toast.makeText(this@DestinationListActivity, " Kamu memilih spinner "+kecamatan, Toast.LENGTH_SHORT).show()
            }

        }

		fab.setOnClickListener {
			val intent = Intent(this@DestinationListActivity, DestinationCreateActivity::class.java)
			startActivity(intent)
		}

	}

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        toolbar.inflateMenu(R.menu.main_menu)
        val searchView = menu!!.findItem(R.id.search)!!.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {


                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                destinationAdapter.filter.filter(newText)
                return false
            }
        })

        searchView.setOnCloseListener {
            showLoading(true)
            true
        }

        return super.onCreateOptionsMenu(menu)
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

        loadDestination()
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
                txv_jumlah_destinasi.text = destination.size.toString()
                showLoading(false)
            }
        })
    }

    private fun initSpinnerKabupaten(){

        apiService.getKabupaten().enqueue(object : Callback<KabupatenResponse>{
            override fun onFailure(call: Call<KabupatenResponse>, t: Throwable) {
                Toast.makeText(this@DestinationListActivity, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this@DestinationListActivity, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
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
                    Log.d("berhasilResponse", response.toString())
                }

                else{
                    Log.d("gagalresponse", response.toString())
                    Toast.makeText(this@DestinationListActivity, "Gagal mengambil spinner", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }



}









