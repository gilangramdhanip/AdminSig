package com.skripsi.sigwam

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emoji.adminsig.preferencetools.SessionManager
import com.skripsi.sigwam.activity.GantiEmailActivity
import com.skripsi.sigwam.activity.DestinationListActivity
import com.skripsi.sigwam.activity.GantiNamaActivity
import com.skripsi.sigwam.activity.GantiPasswordActivity
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        val simpanEmail = sessionManager.getEmail()
        val hasil = simpanEmail.replace(Regex("(?<=.{3}).(?=.*@)"), "*")

        email.text = hasil
        nama.text = sessionManager.getNama()

        if (sessionManager.isLoggedIn()){
            login_btn.visibility = View.GONE
            card_ll.visibility = View.VISIBLE
            txv_login.visibility = View.GONE

            btn_logout.setOnClickListener {
                sessionManager.clearSession()
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            card_email.setOnClickListener {
                val intent = Intent(context, GantiEmailActivity::class.java)
                startActivity(intent)
            }

            card_nama.setOnClickListener {
                val intent = Intent(context, GantiNamaActivity::class.java)
                startActivity(intent)
            }

            card_password.setOnClickListener {
                val intent = Intent(context, GantiPasswordActivity::class.java)
                startActivity(intent)
            }

            card_wisata.setOnClickListener {
                val intent = Intent(context, DestinationListActivity::class.java)
                startActivity(intent)
            }
        }else{
            card_ll.visibility = View.GONE
            login_btn.visibility = View.VISIBLE
            txv_login.visibility = View.VISIBLE
        }

        login_btn.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java )
            startActivity(intent)
        }
    }

}
