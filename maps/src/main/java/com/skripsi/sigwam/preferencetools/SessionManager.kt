package com.emoji.adminsig.preferencetools

import android.content.Context
import android.content.SharedPreferences

class SessionManager(val context: Context) {
    val PREFSNAME = "SharedLoginPreference"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFSNAME, Context.MODE_PRIVATE)

    fun saveData(id_wisatawan: String,
                 email: String,
                 nama: String,
                 password: String
    ){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean("LOGIN_STATUS", true)
        editor.putString("ID", id_wisatawan)
        editor.putString("EMAIL", email)
        editor.putString("NAMA", nama)
        editor.putString("PASS", password)
        editor.apply()
    }

    fun getId(): String {
        return sharedPref.getString("ID", null).toString()
    }

    fun getEmail(): String{
        return sharedPref.getString("EMAIL", null).toString()
    }

    fun getNama(): String{
        return sharedPref.getString("NAMA", null).toString()
    }

    fun getPassword(): String{
        return sharedPref.getString("PASS", null).toString()
    }

    fun isLoggedIn(): Boolean{
        return sharedPref.getBoolean("LOGIN_STATUS", false)
    }

    fun clearSession(){
        val editor : SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.commit()
    }
}