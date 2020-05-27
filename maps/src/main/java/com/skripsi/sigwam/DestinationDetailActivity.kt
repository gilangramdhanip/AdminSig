package com.skripsi.sigwam

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.skripsi.sigwam.model.Destination
import com.skripsi.sigwam.service.ServiceBuilder
import kotlinx.android.synthetic.main.activity_destiny_detail.*
import kotlinx.android.synthetic.main.list_item.view.*


class DestinationDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DETAIl = "extra_detail"
    }

    private var ambil :Int = 0

    private var destination : Destination? = null
    private val apiService = ServiceBuilder.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destiny_detail)

        supportActionBar?.hide()

        destination = intent.getParcelableExtra(EXTRA_DETAIl) as? Destination

        //Load Destination

        destination?.id_destination
        txv_title.text = destination?.name_destination
        txv_address.text = destination?.address_destination
        txv_desc.text = destination?.desc_destination
        if(destination?.img_destination == ""){
            imv_destination.setImageResource(R.drawable.default_img)
        }else{
            Glide.with(baseContext)
                .load("http://192.168.1.71/rest_api/rest-server-sig/assets/foto/"+destination?.img_destination)
                .apply(RequestOptions().override(1280, 720))
                .into(imv_destination)
        }
        txv_cat.text = destination?.id_kategori
        txv_kabupaten.text = destination?.id_kabupaten
        txv_kecamatan.text = destination?.id_kecamatan
        txv_jambuka.text = destination?.jambuka
        txv_jamtutup.text = destination?.jamtutup

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        btn_go_maps.setOnClickListener(this)

    }

//    override fun onClick(v: View?) {
//        val mBundle = Bundle()
//        mBundle.putString(MapsFragment.EXTRA_LAT, destination.toString())
//        mBundle.putString(MapsFragment.EXTRA_LONG, destination.toString())
//        val mapsFragment = MapsFragment()
//        mapsFragment.arguments = mBundle
//
//        supportFragmentManager.beginTransaction().replace(R.id.container, mapsFragment, MapsFragment::class.java.simpleName).commit()
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this,"back key is pressed", Toast.LENGTH_SHORT).show()
    }
}
