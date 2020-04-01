package com.emoji.adminsig.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emoji.adminsig.R
import com.emoji.adminsig.helpers.DestinationAdapter
import com.emoji.adminsig.models.*
import com.emoji.adminsig.services.DestinationService
import com.emoji.adminsig.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_destiny_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DestinationListActivity : AppCompatActivity() {

    private val apiService = ServiceBuilder.create()

    private lateinit var destinationAdapter: DestinationAdapter
    private lateinit var mainViewModel: MainViewModel
    private val destination = ArrayList<Destination>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_destiny_list)

		setSupportActionBar(toolbar)
		toolbar.title = title

        setAllSpinner()


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
                showLoading(false)
            }
        })
    }

    private fun setAllSpinner() {
        setKabupatenSpinner()
//        setMerkSpinner()
//        setJenisSpinner()
//        setModelSpinner()
//        setWarnaSpinner()
//        setBahanBakarSpinner()
//        setWarnaTNKBSpinner()
//        setJenisDaftaranSpinner()
//        setPeruntukanSpinner()
//        setCaraImporSpinner()
    }



    private fun setKabupatenSpinner() {
        apiService.getKabupaten().enqueue(object : Callback<Array<Kabupaten>>{
            override fun onResponse(call: Call<Array<Kabupaten>>, response: Response<Array<Kabupaten>>) {
                if (response.isSuccessful){
                    val data = response.body()
                    val arrayKabupaten = arrayOfNulls<String>(data!!.size)
                    var index = 0
                    data.map {
                        arrayKabupaten[index] = it.name_kabupaten
                        index++

                    }
                    val aa = ArrayAdapter(this@DestinationListActivity, R.layout.spinner_dropdown_item, arrayKabupaten)

                    aa.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    spin_kabupaten.adapter = aa
                    spin_kabupaten.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val index = data[position].id_kabupaten
                            textProvinsi.text=index
                            setKecamatanSpinner(index)
                            Log.d("success", response.toString())
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Array<Kabupaten>>, t: Throwable) {
                Log.d("error", "${t.message}")
            }
        })
    }


    private fun setKecamatanSpinner(idkabupaten: String){
        apiService.getKecamatan(idkabupaten).enqueue(object : Callback<Array<Kecamatan>>{
            override fun onResponse(call: Call<Array<Kecamatan>>, response: Response<Array<Kecamatan>>) {
                if (response.isSuccessful){
                    val data = response.body()
                    val arrayKecamatan = arrayOfNulls<String>(data!!.size)
                    var index = 0
                    data.map {
                        arrayKecamatan[index] = it.name_kecamatan
                        index++
                        Log.d("success", response.toString())

                    }

                    val aa = ArrayAdapter(this@DestinationListActivity, R.layout.spinner_dropdown_item, arrayKecamatan)

                    aa.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    spin_kecamatan.adapter = aa
                    spin_kecamatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val index = data[position].id_kecamatan
                            textKecamatan.text=index
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Array<Kecamatan>>, t: Throwable) {
                Log.d("error", "${t.message}")
            }
        })
    }



}









