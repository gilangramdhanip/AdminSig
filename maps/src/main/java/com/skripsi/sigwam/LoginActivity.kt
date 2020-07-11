package com.skripsi.sigwam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.emoji.adminsig.preferencetools.SessionManager
import com.skripsi.sigwam.activity.GantiPasswordActivity
import com.skripsi.sigwam.activity.LupaPassword
import com.skripsi.sigwam.model.LoginResponse
import com.skripsi.sigwam.service.ServiceBuilder
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registration.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(){

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()){
            val toMenu = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(toMenu)
        }

        button_masuk.setOnClickListener{
            val username = ed_email.text.toString()
            val password = ed_password.text.toString()
            if(TextUtils.isEmpty(username)){
                ed_email.error = "Email tidak boleh kosong"
            }else if(TextUtils.isEmpty(password)){
                ed_password.error = "Password tidak boleh kosong"
            }else{
                signin(username, password)
            }

        }

        btn_daftar.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
        lupa_password.setOnClickListener {
            val intent = Intent(this@LoginActivity, LupaPassword::class.java)
            startActivity(intent)
        }
    }


    private fun signin(username: String, password: String){
        val retIn = ServiceBuilder.create()
        retIn.signin(username, password).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    "Email atau Password anda salah",
                    Toast.LENGTH_SHORT
                ).show()
            }
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    // Set Logged In statue to 'true'
                    val data = response.body()

                    sessionManager.saveData(
                        data!!.data.id_wisatawan,
                        data!!.data.email,
                        data!!.data.nama,
                        data!!.data.password
                    )
                    Log.d("error apanih", response.message())
                    val toLogin = Intent(this@LoginActivity, MainActivity::class.java)
                    toLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(toLogin)
                }
                else{
                    Toast.makeText(this@LoginActivity, "Silahkan Cek Koneksi anda" , Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
