package com.example.ecotrack
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDevices)

        val devices = listOf(
            Device("Living Room AC", "HVAC • 2 min ago", "3.2 kWh", true),
            Device("Kitchen Fridge", "Appliance • 5 min ago", "1.8 kWh", true),
            Device("Bedroom Lights", "Lighting • 1 min ago", "0.4 kWh", true),
            Device("Washer", "Appliance • 3 hrs ago", "Off", false),
            Device("EV Charger", "Transport • 8 min ago", "5.1 kWh", true),
            Device("Water Heater", "HVAC • 15 min ago", "2.3 kWh", true)
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DeviceAdapter(devices)
    }
}