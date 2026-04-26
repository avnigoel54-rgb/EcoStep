package com.example.ecotrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var pairCard: LinearLayout
    private lateinit var deviceAdapter: DeviceAdapter
    private val deviceList = mutableListOf<Device>()
    private var isPairCardVisible = false

    private val manualSetupLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val name = data?.getStringExtra("device_name") ?: return@registerForActivityResult
                val type = data.getStringExtra("device_type") ?: "Device"
                val power = data.getStringExtra("device_power") ?: "0.0 kWh"
                val isOn = data.getBooleanExtra("device_status", true)

                val newDevice = Device(
                    name = name,
                    subtitle = "$type • just now",
                    power = power,
                    isOn = isOn
                )

                addDeviceAndSave(newDevice)
                pairCard.visibility = View.GONE
                isPairCardVisible = false
                Toast.makeText(this, "Device added successfully", Toast.LENGTH_SHORT).show()
            }
        }

    private val qrSetupLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val name = data?.getStringExtra("device_name") ?: return@registerForActivityResult
                val type = data.getStringExtra("device_type") ?: "Scanned Device"
                val power = data.getStringExtra("device_power") ?: "1.0 kWh"
                val isOn = data.getBooleanExtra("device_status", true)

                val newDevice = Device(
                    name = name,
                    subtitle = "$type • just now",
                    power = power,
                    isOn = isOn
                )

                addDeviceAndSave(newDevice)
                pairCard.visibility = View.GONE
                isPairCardVisible = false
                Toast.makeText(this, "QR device added successfully", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDevices)
        val btnThemeToggle = findViewById<ImageButton>(R.id.btnThemeToggle)
        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        val btnAddDevice = findViewById<ImageButton>(R.id.btnAddDevice)
        val btnScanQr = findViewById<Button>(R.id.btnScanQr)
        val btnManualSetup = findViewById<Button>(R.id.btnManualSetup)
        val navDevices = findViewById<TextView>(R.id.navDevices)
        val navTips = findViewById<TextView>(R.id.navTips)

        pairCard = findViewById(R.id.pairCard)

        deviceList.addAll(loadDevices())

        deviceAdapter = DeviceAdapter(deviceList) { position ->
            showDeleteDialog(position)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = deviceAdapter
        recyclerView.isNestedScrollingEnabled = false

        btnAddDevice.setOnClickListener {
            isPairCardVisible = !isPairCardVisible
            pairCard.visibility = if (isPairCardVisible) View.VISIBLE else View.GONE
        }

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

        btnScanQr.setOnClickListener {
            qrSetupLauncher.launch(Intent(this, QrSetupActivity::class.java))
        }

        btnManualSetup.setOnClickListener {
            manualSetupLauncher.launch(Intent(this, ManualSetupActivity::class.java))
        }

        navDevices.setOnClickListener {
            // already on Devices
        }

        navTips.setOnClickListener {
            startActivity(Intent(this, TipsActivity::class.java))
        }
    }

    private fun showDeleteDialog(position: Int) {
        if (position !in deviceList.indices) return

        val deviceName = deviceList[position].name

        AlertDialog.Builder(this)
            .setTitle("Remove Device")
            .setMessage("Do you want to remove $deviceName?")
            .setPositiveButton("Yes") { _, _ ->
                deviceList.removeAt(position)
                deviceAdapter.removeDevice(position)
                saveDevices(deviceList)
                Toast.makeText(this, "$deviceName removed", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun addDeviceAndSave(device: Device) {
        deviceAdapter.addDevice(device)
        saveDevices(deviceList)
    }

    private fun loadDevices(): MutableList<Device> {
        val sharedPreferences = getSharedPreferences("ecotrack_prefs", Context.MODE_PRIVATE)
        val savedJson = sharedPreferences.getString("saved_devices", null)

        if (savedJson.isNullOrEmpty()) {
            return mutableListOf(
                Device("Living Room AC", "HVAC • 2 min ago", "3.2 kWh", true),
                Device("Kitchen Fridge", "Appliance • 5 min ago", "1.8 kWh", true),
                Device("Bedroom Lights", "Lighting • 1 min ago", "0.4 kWh", true),
                Device("Washer", "Appliance • 3 hrs ago", "Off", false),
                Device("EV Charger", "Transport • 8 min ago", "5.1 kWh", true),
                Device("Water Heater", "HVAC • 15 min ago", "2.3 kWh", true)
            )
        }

        val devices = mutableListOf<Device>()
        val jsonArray = JSONArray(savedJson)

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            devices.add(
                Device(
                    name = item.getString("name"),
                    subtitle = item.getString("subtitle"),
                    power = item.getString("power"),
                    isOn = item.getBoolean("isOn")
                )
            )
        }

        return devices
    }

    private fun saveDevices(devices: List<Device>) {
        val jsonArray = JSONArray()

        devices.forEach { device ->
            val jsonObject = JSONObject().apply {
                put("name", device.name)
                put("subtitle", device.subtitle)
                put("power", device.power)
                put("isOn", device.isOn)
            }
            jsonArray.put(jsonObject)
        }

        getSharedPreferences("ecotrack_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("saved_devices", jsonArray.toString())
            .apply()
    }
}



