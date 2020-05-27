package com.skripsi.sigwam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.skripsi.sigwam.model.Destination
import com.skripsi.sigwam.service.ServiceBuilder
import kotlinx.android.synthetic.main.activity_destiny_detail.*


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

        btn_share.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, destination?.id_destination)
                var shareMessage = "\nWisata ${destination?.name_destination} berada di Kabupaten ${destination?.id_kabupaten} dan Kecamatan ${destination?.id_kecamatan}" +
                        "\nSelengkapnya ada di Aplikasi Siwita, Yuk Download\n\n"
                shareMessage =
                    """
                    ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}

                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "Pilih Metode"))
            } catch (e: Exception) {
                //e.toString();
            }
        }

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
