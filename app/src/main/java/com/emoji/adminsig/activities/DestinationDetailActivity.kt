package com.emoji.adminsig.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emoji.adminsig.R
import com.emoji.adminsig.models.DeleteResponse
import com.emoji.adminsig.models.Destination
import com.emoji.adminsig.models.DestinationResponse
import com.emoji.adminsig.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_destiny_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DestinationDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DETAIl = "extra_detail"
    }

    private lateinit var destination : Destination

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destiny_detail)

        destination = intent.getParcelableExtra(EXTRA_DETAIl) as Destination

//        setSupportActionBar(detail_toolbar)
        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        loadDetails()

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
                val cat = et_category.text.toString()
                val img = et_image.text.toString()
                val kec = et_kecamatan.text.toString()
                val admin = et_admin.text.toString()

                val destinationService = ServiceBuilder.create()
                val requestCall = destinationService.updateDestination(destination.id_destination, name, lat, lng, address, description, cat,img, kec, admin)

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

    private fun loadDetails() {

                        destination.id_destination
                        et_name.setText(destination.name_destination)
                        et_latitude.setText(destination.lat_destination)
                        et_longitude.setText(destination.lng_destination)
                        et_address.setText(destination.address_destination)
                        et_description.setText(destination.desc_destination)
                        et_image.setText(destination.img_destination)
                        et_category.setText(destination.category_destination)
                        et_kecamatan.setText(destination.id_kecamatan)
                        et_admin.setText(destination.id_admin)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            navigateUpTo(Intent(this, DestinationListActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
