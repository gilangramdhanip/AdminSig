package com.skripsi.sigwam

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.skripsi.sigwam.model.Destination
import com.skripsi.sigwam.service.ServiceBuilder
import kotlinx.android.synthetic.main.activity_destiny_detail.*


class DestinationDetailActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val EXTRA_DETAIl = "extra_detail"
    }

    private var ambil :Int = 0

    private lateinit var destination : Destination
    private val apiService = ServiceBuilder.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destiny_detail)

        supportActionBar?.hide()

        destination = intent.getParcelableExtra(EXTRA_DETAIl) as Destination

        //Load Destination

        destination.id_destination
        txv_title.text = destination.name_destination
        txv_address.text = destination.address_destination
        txv_desc.text = destination.desc_destination
//        et_image.setText(destination.img_destination)
        txv_cat.text = destination.category_destination
        txv_kabupaten.text = destination.id_kabupaten
        txv_kecamatan.text = destination.id_kecamatan

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_go_maps.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        val mBundle = Bundle()
        mBundle.putString(MapsFragment.EXTRA_LAT, destination.toString())
        mBundle.putString(MapsFragment.EXTRA_LONG, destination.toString())
        val mapsFragment = MapsFragment()
        mapsFragment.arguments = mBundle

        supportFragmentManager.beginTransaction().replace(R.id.container, mapsFragment, MapsFragment::class.java.simpleName).commit()
    }
}
