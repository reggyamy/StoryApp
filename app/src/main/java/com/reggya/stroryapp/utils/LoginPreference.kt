package com.reggya.stroryapp.utils

import android.content.Context

internal class LoginPreference(context: Context) {

    companion object{
        private const val KEY_PREFS = "user_pref"
        const val KEY_LOGIN_NAME = "name"
        const val KEY_ID = "id_user"
        const val KEY_TOKEN ="token"
        private const val ISLOGIN = "false"
    }

    private val preference = context.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE)

    fun removeAll(){
        val editor = preference.edit()
        editor.clear()
        editor.apply()
    }

    fun setLogin(name: String, userId: String, token: String){
        val editor = preference.edit()
        editor.putString(KEY_ID, userId)
        editor.putString(KEY_TOKEN, token)
        editor.putString(KEY_LOGIN_NAME, name)
        editor.apply()
    }

    fun getName()= preference.getString(KEY_LOGIN_NAME, "")
    fun getToken() = preference.getString(KEY_TOKEN, "")
    fun getUserId() = preference.getString(KEY_ID, "")

    fun isLogin(state : Boolean){
        val editor = preference.edit()
        editor.putBoolean(ISLOGIN, state)
            .apply()
    }

    fun getIsLogin(): Boolean{
        return preference.getBoolean(ISLOGIN, false)
    }
}