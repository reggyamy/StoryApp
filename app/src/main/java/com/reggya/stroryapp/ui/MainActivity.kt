package com.reggya.stroryapp.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.reggya.stroryapp.utils.LoginPreference
import com.reggya.stroryapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            val loginPreference = LoginPreference(this)
            if (loginPreference.getIsLogin()) startActivity(Intent(this, StoriesActivity::class.java))
            else startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }, 2000)
    }
}