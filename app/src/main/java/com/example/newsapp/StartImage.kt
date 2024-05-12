package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StartImage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start_image)
        supportActionBar?.hide()

        Handler().postDelayed({
            val intent = Intent(this@StartImage,LoginActivity::class.java)
            startActivity(intent)
            finish()
        },1000)
    }
}