package com.skripsi.sigwam.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.emoji.adminsig.preferencetools.SessionManager
import com.google.android.material.snackbar.Snackbar
import com.skripsi.sigwam.MainActivity
import com.skripsi.sigwam.R
import com.skripsi.sigwam.model.DestinationResponse
import com.skripsi.sigwam.model.WisatawanResponse
import com.skripsi.sigwam.service.ServiceBuilder
import kotlinx.android.synthetic.main.activity_ganti_email.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GantiEmailActivity : AppCompatActivity() {

    private lateinit var sessionManager : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti_email)
        val toolbar: Toolbar? = findViewById<Toolbar>(R.id.profileToolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)

        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        sessionManager = SessionManager(this)


        update_email.setOnClickListener {
            if(Editable.Factory.getInstance().newEditable(email_lama.text).toString() != sessionManager.getEmail()){
                Toast.makeText(this@GantiEmailActivity,"Email lama salah", Toast.LENGTH_LONG).show()
            }else{
                val simpanEmailBaru = email_baru.text.toString()
                val id = sessionManager.getId()
                val nama = sessionManager.getNama()
                val password = sessionManager.getPassword()
                val destinationService = ServiceBuilder.create()
                val requestCall = destinationService.updateWisatawan(id, simpanEmailBaru, nama, password)

                requestCall.enqueue(object: Callback<WisatawanResponse> {

                    override fun onResponse(call: Call<WisatawanResponse>, response: Response<WisatawanResponse>) {
                        if (response.isSuccessful) {

                            sessionManager.saveData(
                                id,
                                simpanEmailBaru,
                                nama,
                                password
                            )
                            val intent = Intent(this@GantiEmailActivity, MainActivity::class.java)
                            startActivity(intent)
                            Snackbar.make(it, "Email Berhasil diubah", Snackbar.LENGTH_LONG).show()
                            finishAffinity()

                            Log.d("onResponse", response.toString())
                        } else {
                            Snackbar.make(it, "Data Gagal diubah", Snackbar.LENGTH_LONG).show()
                            Log.d("respongagal", response.toString())
                        }
                    }

                    override fun onFailure(call: Call<WisatawanResponse>, t: Throwable) {
                        Snackbar.make(it, "Data Gagal diubah bro", Snackbar.LENGTH_LONG).show()
                        Log.d("respon gagagal", t.message)
                    }
                })
            }
        }
    }
}
