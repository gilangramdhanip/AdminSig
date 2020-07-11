package com.skripsi.sigwam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.skripsi.sigwam.model.Wisatawan
import com.skripsi.sigwam.model.WisatawanResponse
import com.skripsi.sigwam.service.ServiceBuilder
import kotlinx.android.synthetic.main.activity_registration.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)


        button_daftar.setOnClickListener {

            if(edt_email_wis.text.isNullOrEmpty() && edt_nama_wis.text.isNullOrEmpty() && edt_password_wis.text.isNullOrEmpty()){
                edt_email_wis.setError("Email tidak boleh kosong")
                edt_nama_wis.setError("Nama tidak boleh kosong")
                edt_password_wis.setError("Password tidak boleh kosong")
            }else{
                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

                if(edt_email_wis.text.toString().trim().matches(emailPattern.toRegex())) {

                    val newWisatawan = Wisatawan(taskId)
                    newWisatawan.email = edt_email_wis.text.toString()
                    newWisatawan.nama = edt_nama_wis.text.toString()
                    newWisatawan.password = edt_password_wis.text.toString()

                    val pembeliService = ServiceBuilder.create()
                    val requestCall = pembeliService.regisWisatawan(newWisatawan)

                    requestCall.enqueue(object : Callback<WisatawanResponse> {

                        override fun onResponse(
                            call: Call<WisatawanResponse>,
                            response: Response<WisatawanResponse>
                        ) {
                            if (response.isSuccessful) {
                                val intent = Intent(
                                    this@RegistrationActivity,
                                    LoginActivity::class.java
                                ) // Move back to BarangListActivity
                                startActivity(intent)
                                finish()
                                var newlyCreatedPembeli = response.body() // Use it or ignore it
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Successfully Added",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d("ResponseLog", response.toString())
                            } else {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Email sudah terdaftar",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d("gagalLog", response.toString())
                            }
                        }

                        override fun onFailure(call: Call<WisatawanResponse>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                "Failed to add item",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            Log.d("FailureLog", t.message)
                        }
                    })
                }else{
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Format email salah",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
