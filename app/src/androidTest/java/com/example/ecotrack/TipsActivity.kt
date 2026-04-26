package com.example.ecotrack

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class TipsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tips)

        val btnThemeToggle = findViewById<ImageButton>(R.id.btnThemeToggle)
        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        val navDevices = findViewById<TextView>(R.id.navDevices)
        val navTips = findViewById<TextView>(R.id.navTips)
        val btnAiSuggestions = findViewById<TextView>(R.id.btnAiSuggestions)

        btnThemeToggle.setOnClickListener {
            val currentMode = AppCompatDelegate.getDefaultNightMode()
            val newMode = if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            AppCompatDelegate.setDefaultNightMode(newMode)
        }

        btnLogout.setOnClickListener {
            Toast.makeText(this, "Sign-in page is not added yet", Toast.LENGTH_SHORT).show()
        }

        navDevices.setOnClickListener {
            finish()
        }

        navTips.setOnClickListener {
            Toast.makeText(this, "You are already on Tips", Toast.LENGTH_SHORT).show()
        }

        btnAiSuggestions.setOnClickListener {
            Toast.makeText(this, "AI suggestions logic can be added next", Toast.LENGTH_SHORT).show()
        }
    }
}
