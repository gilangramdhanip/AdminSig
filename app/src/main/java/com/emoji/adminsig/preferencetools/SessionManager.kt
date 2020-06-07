package com.emoji.adminsig.preferencetools

import android.content.Context
import android.content.SharedPreferences

class SessionManager(val context: Context) {
    val PREFSNAME = "SharedLoginPreference"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFSNAME, Context.MODE_PRIVATE)

    fun saveData(id_admin: String,
                 username: String,
                 password: String
    ){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean("LOGIN_STATUS", true)
        editor.putString("ID", id_admin)
        editor.putString("USERNAME", username)
        editor.putString("PASS", password)
        editor.apply()
    }

    fun getId(): String {
        return sharedPref.getString("ID", null).toString()
    }

    fun getUsername(): String{
        return sharedPref.getString("USERNAME", null).toString()
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