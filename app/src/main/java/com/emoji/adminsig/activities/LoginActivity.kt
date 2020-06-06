package com.emoji.adminsig.activities

import SessionManager
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emoji.adminsig.R
import com.emoji.adminsig.models.LoginResponse
import com.emoji.adminsig.services.ServiceBuilder
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Stetho.initializeWithDefaults(this)

        sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()){
            val toMenu = Intent(this@LoginActivity, DestinationListActivity::class.java)
            startActivity(toMenu)
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
                    val data = response.body()

                    sessionManager.saveData(
                        data!!.data.id_admin,
                        data!!.data.username,
                        data!!.data.password

                    )
                    Log.d("error apanih", response.message())
                    val toLogin = Intent(this@LoginActivity, DestinationListActivity::class.java)
                    toLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(toLogin)
                }
            }
        })
    }
}
