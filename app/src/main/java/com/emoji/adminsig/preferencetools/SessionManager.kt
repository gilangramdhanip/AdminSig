package com.emoji.adminsig.preferencetools

import android.content.Context
import android.content.SharedPreferences

class SessionManager(val context: Context) {
    val PREFSNAME = "SharedLoginPreference"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFSNAME, Context.MODE_PRIVATE)

    fun saveData(username: String,
                 password: String,
                 id_admin: String){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString("ID_ADMIN", id_admin)
        editor.putBoolean("LOGIN_STATUS", true)
        editor.putString("USERNAME", username)
        editor.putString("PASSWORD", password)
        editor.apply()
    }

    fun getId(): String{
        return sharedPref.getString("ID_ADMIN", null).toString()
    }

    fun getUsername(): String{
        return sharedPref.getString("USERNAME", null).toString()
    }

    fun getPassword(): String{
        return sharedPref.getString("PASSWORD", null).toString()
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
