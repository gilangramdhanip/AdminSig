package com.skripsi.sigwam

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.emoji.adminsig.preferencetools.SessionManager
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.getkeepsafe.taptargetview.TapTargetView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object{
        lateinit var contextOfApp: Context
    fun getContextApp(): Context {
        return contextOfApp
    }
}
    lateinit var navController : NavController
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sessionManager = SessionManager(this)
        Toast.makeText(this@MainActivity, sessionManager.getEmail(), Toast.LENGTH_SHORT).show()

        contextOfApp = applicationContext

        supportActionBar?.hide()


        navController = findNavController(R.id.nav_host)
//        val appBarConfiguration = AppBarConfiguration.Builder(
//            R.id.navigation_home, R.id.navigation_map, R.id.navigation_list
//        ).build()

        setupActionBarWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu,menu)
        nav_bottom.setupWithNavController(menu!!,navController)


        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

//    fun showTapView() {
//
//    }

}
