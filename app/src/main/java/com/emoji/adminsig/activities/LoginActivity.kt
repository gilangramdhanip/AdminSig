package com.emoji.adminsig.activities

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emoji.adminsig.R
import com.emoji.adminsig.helpers.DestinationAdapter
import com.emoji.adminsig.helpers.SaveSharedPreference
import com.emoji.adminsig.helpers.SaveSharedPreference.setLoggedIn
import com.emoji.adminsig.models.LoginResponse
import com.emoji.adminsig.services.ServiceBuilder
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Stetho.initializeWithDefaults(this)

        if (SaveSharedPreference.getLoggedStatus(applicationContext)) {
            val intent = Intent(applicationContext, DestinationListActivity::class.java)
            startActivity(intent)
        }
        btn_login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        val username = edt_username.text.toString()
        val password = edt_password.text.toString()
        signin(username, password)
    }

    private fun signin(username: String, password: String){
        val retIn = ServiceBuilder.create()
        retIn.signin(username, password).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    // Set Logged In statue to 'true'
                    setLoggedIn(applicationContext, true)
                    val intent = Intent(applicationContext, DestinationListActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
                    intent.putExtra("username", username)
                    Toast.makeText(
                        applicationContext, "Selamat datang admin $username",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        applicationContext, "Password/username salah",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}
