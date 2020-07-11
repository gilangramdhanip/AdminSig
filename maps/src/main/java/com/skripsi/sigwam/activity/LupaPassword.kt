package com.skripsi.sigwam.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.skripsi.sigwam.LoginActivity
import com.skripsi.sigwam.MainActivity
import com.skripsi.sigwam.R
import com.skripsi.sigwam.model.LoginResponse
import com.skripsi.sigwam.model.Wisatawan
import com.skripsi.sigwam.model.WisatawanResponse
import com.skripsi.sigwam.service.ServiceBuilder
import kotlinx.android.synthetic.main.activity_lupa_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LupaPassword : AppCompatActivity() {

    lateinit var wisatawan: Wisatawan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupa_password)

        val toolbar: Toolbar? = findViewById<Toolbar>(R.id.profileToolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)

        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        cek_email.setOnClickListener {
            if(input_email.text.isNullOrEmpty()){
                input_email.setError("Email tidak boleh kosong")
            }else{
                cekEmail(input_email.text.toString())
            }
        }
    }


    private fun cekEmail(email : String){
        val retIn = ServiceBuilder.create()
        retIn.lupaPassword(email).enqueue(object : Callback<WisatawanResponse> {
            override fun onFailure(call: Call<WisatawanResponse>, t: Throwable) {
                Toast.makeText(this@LupaPassword, "Email Belum terdaftar" , Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<WisatawanResponse>, response: Response<WisatawanResponse>) {
                if (response.isSuccessful) {
                    // Set Logged In statue to 'true'
                    val data = response.body()
                    val intent = Intent(this@LupaPassword, GantiPasswordLupaPassword::class.java)
                    intent.putExtra("ID", data!!.data.id_wisatawan.toString())
                    intent.putExtra("EMAIL", data!!.data.email.toString())
                    intent.putExtra("NAMA", data!!.data.nama.toString())
                    startActivity(intent)
                    Log.d("error apanih", response.message())
                }
                else{
                    Toast.makeText(this@LupaPassword, "Email yang anda inputkan salah/belum terdaftar" , Toast.LENGTH_LONG).show()
                    Log.d("respongagal", response.toString())
                }
            }
        })
    }
}
