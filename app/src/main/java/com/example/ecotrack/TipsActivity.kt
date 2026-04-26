package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class TipsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tips)

        val btnThemeToggle = findViewById<ImageButton>(R.id.btnThemeToggle)
        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        
        val navHome = findViewById<TextView>(R.id.navHome)
        val navDevices = findViewById<TextView>(R.id.navDevices)
        val navAnalytics = findViewById<TextView>(R.id.navAnalytics)
        val navGoals = findViewById<TextView>(R.id.navGoals)
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
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
        }

        navHome.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        navDevices.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }
        
        navAnalytics.setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java))
            finish()
        }

        navGoals.setOnClickListener {
            Toast.makeText(this, "Goals coming soon", Toast.LENGTH_SHORT).show()
        }

        navTips.setOnClickListener {
            // Already here
        }

        btnAiSuggestions.setOnClickListener {
            showAiSuggestionsDialog()
        }
    }

    private fun showAiSuggestionsDialog() {
        val suggestions = arrayOf(
            "Replace your old refrigerator with an A+++ rated model to save up to 40% energy.",
            "Install a smart thermostat to optimize heating and cooling schedules.",
            "Use heavy curtains during winter to retain heat and reduce heater usage.",
            "Consider solar panels if your roof gets at least 4 hours of direct sunlight.",
            "Upgrade to smart plugs to monitor and cut off standby power for electronics."
        )

        AlertDialog.Builder(this)
            .setTitle("AI Energy Suggestions")
            .setItems(suggestions) { _, which ->
                Toast.makeText(this, "Excellent choice! Adding to goals...", Toast.LENGTH_SHORT).show()
            }
            .setPositiveButton("Refresh") { dialog, _ ->
                dialog.dismiss()
                showAiSuggestionsDialog()
            }
            .setNegativeButton("Close", null)
            .show()
    }
}
