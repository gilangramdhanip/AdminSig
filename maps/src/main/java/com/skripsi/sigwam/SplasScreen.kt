package com.skripsi.sigwam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplasScreen : AppCompatActivity() {

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splas_screen)

        supportActionBar?.hide()

        handler = Handler()
        handler.postDelayed({
            val intent = Intent(this@SplasScreen, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000)
    }
}
