package com.example.ecotrack

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.MainActivity as ComposeActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        tvWelcome.text = "Welcome back, ${userName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} 👋"

        // 2. Setup Device List
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDevices)
        deviceList = mutableListOf()
        adapter = DeviceAdapter(deviceList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 3. Setup Pie Chart
        pieChart = findViewById<PieChart>(R.id.pieChart)
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

        // 6. Navigation Bar Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on Dashboard (Home)
                    true
                }
                R.id.nav_devices -> {
                    // Navigate to old UI MainActivity (Devices)
                    startActivity(Intent(this, com.example.ecotrack.MainActivity::class.java))
                    true
                }
                R.id.nav_analytics -> {
                    // Placeholder for Analytics
                    true
                }
                R.id.nav_goals -> {
                    // Navigate to Compose GoalsScreen
                    startActivity(Intent(this, ComposeActivity::class.java))
                    true
                }
                R.id.nav_tips -> {
                    startActivity(Intent(this, TipsActivity::class.java))
                    true
                }
                else -> false
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
