package com.emoji.adminsig.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.emoji.adminsig.AgungActivity
import com.emoji.adminsig.R
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Stetho.initializeWithDefaults(this)
        btn_login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val moveToMain = Intent(applicationContext, DestinationListActivity::class.java)
        startActivity(moveToMain)
    }
}
