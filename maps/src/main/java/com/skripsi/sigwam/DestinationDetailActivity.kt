package com.skripsi.sigwam

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
import com.skripsi.sigwam.model.Destination
import com.skripsi.sigwam.service.ServiceBuilder
import kotlinx.android.synthetic.main.activity_destiny_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class DestinationDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DETAIl = "extra_detail"
    }

    private var ambil :Int = 0

    private lateinit var destination : Destination
    private val apiService = ServiceBuilder.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destiny_detail)

        destination = intent.getParcelableExtra(EXTRA_DETAIl) as Destination

        //Load Destination

        destination.id_destination
        txv_title.setText(destination.name_destination)
        txv_address.setText(destination.address_destination)
        txv_desc.setText(destination.desc_destination)
//        et_image.setText(destination.img_destination)
        txv_cat.setText(destination.category_destination)
        txv_kabupaten.setText(destination.id_kabupaten)
        txv_kecamatan.setText(destination.id_kecamatan)


//        setSupportActionBar(detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        if (id == android.R.id.home) {
//            navigateUpTo(Intent(this, DestinationListActivity::class.java))
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
}
