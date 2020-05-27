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
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contextOfApp = applicationContext

        supportActionBar?.hide()


        navController = findNavController(R.id.nav_host)
//        val appBarConfiguration = AppBarConfiguration.Builder(
//            R.id.navigation_home, R.id.navigation_map, R.id.navigation_list
//        ).build()

        setupActionBarWithNavController(navController)

        showTapView()
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

    fun showTapView() {
        if (StatusUtils.getTutorialStatus(this)) {
            TapTargetSequence(this)
                // 2
                .targets(

                    TapTarget.forView(
                        findViewById(R.id.navigation_list),
                        "List Wisata",
                        "Halaman untuk menampilkan Daftar Wisata Alam"
                    )
                        .cancelable(false).transparentTarget(true).targetRadius(70),
                    TapTarget.forView(
                        findViewById(R.id.navigation_map),
                        "Halaman Peta",
                        "Halaman untuk menampilkan peta Wisata Alam"
                    )
                        .cancelable(false).transparentTarget(true).targetRadius(70)
                )


                // 3
                .listener(object : TapTargetSequence.Listener {
                    override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                    }

                    // 4
                    override fun onSequenceFinish() {
                        Toast.makeText(
                            this@MainActivity, "Berhasil",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    // 5
                    override fun onSequenceCanceled(lastTarget: TapTarget) {
                    }
                })
                // 6
                .start()

        }
    }

}
