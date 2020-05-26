package com.emoji.adminsig.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emoji.adminsig.R
import com.emoji.adminsig.helpers.DestinationAdapter
import com.emoji.adminsig.helpers.PencarianAdapter
import com.emoji.adminsig.helpers.SaveSharedPreference
import com.emoji.adminsig.models.*
import com.emoji.adminsig.preferencetools.SessionManager
import com.emoji.adminsig.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_destiny_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DestinationListActivity : AppCompatActivity() {

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

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_destiny_list)

		setSupportActionBar(toolbar)
		toolbar.title = title

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.keluar) {
            SaveSharedPreference.setLoggedIn(applicationContext, false)
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
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

//    fun pencarian(){
//                search_view.queryHint = resources.getString(R.string.search_hint)
//        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
//            androidx.appcompat.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//
//                return true
//            }
//
//            override fun onQueryTextChange(p0: String?): Boolean {
//                destinationAdapter.filter.filter(p0)
//                return false
//            }
//
//        })
//    }



}









