package com.emoji.adminsig.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.emoji.adminsig.R
import com.emoji.adminsig.helpers.DestinationAdapter
import com.emoji.adminsig.helpers.VerificationAdapter
import com.emoji.adminsig.models.Destination
import com.emoji.adminsig.models.MainViewModel
import kotlinx.android.synthetic.main.activity_destiny_list.*
import kotlinx.android.synthetic.main.activity_destiny_list.progressbar
import kotlinx.android.synthetic.main.activity_destiny_list.toolbar
import kotlinx.android.synthetic.main.activity_destiny_list.txv_jumlah_destinasi
import kotlinx.android.synthetic.main.activity_verifikasi.*

class VerifikasiActivity : AppCompatActivity() {

    private lateinit var destinationAdapter: VerificationAdapter
    private lateinit var mainViewModel: MainViewModel
    private val destination = ArrayList<Destination>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifikasi)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        toolbar.inflateMenu(R.menu.main_menu)
//        val searchView = menu!!.findItem(R.id.search)!!.actionView as SearchView
//        searchView.queryHint = resources.getString(R.string.search_hint)
//        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                destinationAdapter.filter.filter(newText)
//                return false
//            }
//        })
//
//        searchView.setOnCloseListener {
//            showLoading(true)
//            true
//        }
//
//        return super.onCreateOptionsMenu(menu)
//    }

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

        destinationAdapter = VerificationAdapter(destination)
        destinationAdapter.notifyDataSetChanged()

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainViewModel::class.java
        )

        showLoading(true)

        mainViewModel.setDestination()
        verify_rc.setHasFixedSize(true)
        verify_rc.layoutManager = LinearLayoutManager(applicationContext)
        verify_rc.adapter = destinationAdapter

        mainViewModel.getDestination().observe(this, Observer { destination ->
            if(destination!=null){
                destinationAdapter.setData(destination)
                txv_jumlah_destinasi.text = destinationAdapter.itemCount.toString()
                showLoading(false)
            }
        })
    }
}
