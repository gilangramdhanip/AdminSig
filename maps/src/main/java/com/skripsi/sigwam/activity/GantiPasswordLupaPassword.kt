package com.skripsi.sigwam.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.skripsi.sigwam.LoginActivity
import com.skripsi.sigwam.R
import com.skripsi.sigwam.model.WisatawanResponse
import com.skripsi.sigwam.service.ServiceBuilder
import kotlinx.android.synthetic.main.activity_ganti_password_lupa_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GantiPasswordLupaPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti_password_lupa_password)

        val toolbar: Toolbar? = findViewById<Toolbar>(R.id.profileToolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)

        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        val id = intent.getStringExtra("ID")
        val nama = intent.getStringExtra("NAMA")
        val email = intent.getStringExtra("EMAIL")


        nama_akun.setText(nama)

        update_password.setOnClickListener {

            if(input_password.text.toString() == verif_password.text.toString()) {
                val retIn = ServiceBuilder.create()
                retIn.gantiPassword(id, email, nama, verif_password.text.toString()).enqueue(object :
                    Callback<WisatawanResponse> {
                    override fun onFailure(call: Call<WisatawanResponse>, t: Throwable) {
                        Toast.makeText(
                            this@GantiPasswordLupaPassword,
                            t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onResponse(
                        call: Call<WisatawanResponse>,
                        response: Response<WisatawanResponse>
                    ) {
                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@GantiPasswordLupaPassword,
                                "Password berhasil diganti",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent =
                                Intent(this@GantiPasswordLupaPassword, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@GantiPasswordLupaPassword,
                                "Gagal ganti password",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("error 2", response.toString())
                        }
                    }
                })
            }else if(input_password.text.toString() != verif_password.text.toString()){
                Toast.makeText(this@GantiPasswordLupaPassword, "Password tidak boleh berbeda", Toast.LENGTH_SHORT).show()
            }else if(input_password.text.toString() == "" && verif_password.text.toString() == ""){
                Toast.makeText(this@GantiPasswordLupaPassword, "Ganti password gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
