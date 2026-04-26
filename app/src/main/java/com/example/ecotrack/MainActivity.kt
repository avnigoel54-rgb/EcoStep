package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.MainActivity as ComposeActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Setup Device List
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDevices)
        val devices = listOf(
            Device("Smart Fridge", "Kitchen", "1.2 kWh", true),
            Device("AC Unit", "Living Room", "2.5 kWh", true),
            Device("Electric Oven", "Kitchen", "0.0 kWh", false),
            Device("Washing Machine", "Laundry", "0.8 kWh", true)
        )
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DeviceAdapter(devices)

        // 2. Setup Navigation
        findViewById<TextView>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        findViewById<TextView>(R.id.navGoals).setOnClickListener {
            startActivity(Intent(this, ComposeActivity::class.java))
        }
        
        findViewById<TextView>(R.id.navTips).setOnClickListener {
            startActivity(Intent(this, TipsActivity::class.java))
        }

        // 3. Setup Add Device Toggle
        val pairCard = findViewById<LinearLayout>(R.id.pairCard)
        findViewById<View>(R.id.btnAddDevice).setOnClickListener {
            pairCard.visibility = if (pairCard.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }
}
