package com.example.angelocapstoneproject.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.angelocapstoneproject.R
import com.example.angelocapstoneproject.ui.home.Home
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(1500L)
            val intent = Intent(this@Splash, Home::class.java)
            startActivity(intent)
            finish()
        }
    }
}