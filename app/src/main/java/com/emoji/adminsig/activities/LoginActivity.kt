package com.emoji.adminsig.activities

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emoji.adminsig.R
import com.emoji.adminsig.models.LoginResponse
import com.emoji.adminsig.models.SignInBody
import com.emoji.adminsig.services.ServiceBuilder
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_welcome.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Stetho.initializeWithDefaults(this)
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
                if (response.code() == 200) {
                    Toast.makeText(this@LoginActivity, "Login success! $username", Toast.LENGTH_SHORT).show()
                    val loginIntent = Intent(this@LoginActivity, DestinationListActivity::class.java)
                    loginIntent.putExtra("username", username)
                    startActivity(loginIntent)
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed!", Toast.LENGTH_SHORT).show()
                    Log.d("onFailed",response.toString())
                }
            }
        })
    }
}
