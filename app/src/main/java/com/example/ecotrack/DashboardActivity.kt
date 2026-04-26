package com.example.ecotrack

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import android.widget.Switch // Added this
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate // Added this
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardActivity : AppCompatActivity() {

    private lateinit var deviceList: MutableList<Device>
    private lateinit var adapter: DeviceAdapter
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // 1. Dynamic Welcome Message
        val userName = intent.getStringExtra("USER_NAME") ?: "User"
        val tvWelcome = findViewById<TextView>(R.id.tvWelcomeMessage)
        tvWelcome.text = "Welcome back, ${userName.replaceFirstChar { it.uppercase() }} 👋"

        // 2. Setup Device List
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDevices)
        deviceList = mutableListOf()
        adapter = DeviceAdapter(deviceList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 3. Setup Pie Chart
        pieChart = findViewById(R.id.pieChart)
        updatePieChart()

        // 4. Add Device Logic
        val fab = findViewById<FloatingActionButton>(R.id.fabAddDevice)
        fab.setOnClickListener {
            val newDevice = Device("Smart Device ${deviceList.size + 1}", "Home", "1.5 kWh", true)
            deviceList.add(newDevice)
            adapter.notifyItemInserted(deviceList.size - 1)
            updatePieChart()
        }

        // 5. Theme Toggle Logic
        val themeSwitch = findViewById<Switch>(R.id.themeSwitch)

        // Detect current mode to set switch state
        val isNightMode = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
        themeSwitch.isChecked = isNightMode

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun updatePieChart() {
        val entries = ArrayList<PieEntry>()
        if (deviceList.isEmpty()) {
            entries.add(PieEntry(1f, "No Devices"))
            pieChart.centerText = "No Usage Data"
        } else {
            deviceList.forEach { device ->
                val value = device.power.replace(" kWh", "").toFloatOrNull() ?: 0f
                entries.add(PieEntry(value, device.name))
            }
            val totalKwh = deviceList.sumOf { it.power.replace(" kWh", "").toDoubleOrNull() ?: 0.0 }
            pieChart.centerText = "Total\n${String.format("%.1f", totalKwh)} kWh"
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(Color.parseColor("#58B38D"), Color.parseColor("#4A90E2"), Color.parseColor("#F5A623"))
        dataSet.valueTextColor = Color.WHITE

        pieChart.data = PieData(dataSet)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setCenterTextColor(Color.WHITE)
        pieChart.animateY(1000)
        pieChart.invalidate()
    }
}