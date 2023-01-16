package com.example.smartug.session

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.smartug.ListDevice
import com.example.smartug.Login

class LoginPref(private var con: Context) {
    private var pref: SharedPreferences
    private var editor: SharedPreferences.Editor
    private var privateMode: Int = 0

    init {
        pref = con.getSharedPreferences(PREF_NAME, privateMode)
        editor = pref.edit()
    }

    companion object {
        const val PREF_NAME = "LoginPreference"
        const val IS_LOGIN = "isLogIn"
        const val KEY_EMAIL = "email"
        const val KEY_Passwd = "passwd"
    }

    fun createLoginSession(email: String, passwd: String) {
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_Passwd, passwd)
        editor.commit()
    }

    fun checkLogin() {
        if (!this.islogedIn()) {
            val i = Intent(con, ListDevice::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            con.startActivity(i)
        }
    }

    fun getUserDetails(): HashMap<String, String> {
        val user: Map<String, String> = HashMap()
        (user as HashMap)[KEY_EMAIL] = pref.getString(KEY_EMAIL, null)!!
        user[KEY_Passwd] = pref.getString(KEY_Passwd, null)!!
        return user
    }

    fun logOutUser() {
        editor.clear()
        editor.commit()
        val i = Intent(con, Login::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        con.startActivity(i)
    }

    fun islogedIn(): Boolean {
        return pref.getBoolean(IS_LOGIN, false)
    }
}