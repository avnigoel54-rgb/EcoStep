package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ManualSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_setup)

        val etDeviceName = findViewById<EditText>(R.id.etDeviceName)
        val etDeviceType = findViewById<EditText>(R.id.etDeviceType)
        val etDevicePower = findViewById<EditText>(R.id.etDevicePower)
        val cbDeviceStatus = findViewById<CheckBox>(R.id.cbDeviceStatus)
        val btnSaveManual = findViewById<Button>(R.id.btnSaveManual)
        val btnBackFromManual = findViewById<Button>(R.id.btnBackFromManual)

        btnBackFromManual.setOnClickListener {
            finish()
        }

        btnSaveManual.setOnClickListener {
            val name = etDeviceName.text.toString().trim()
            val type = etDeviceType.text.toString().trim()
            val power = etDevicePower.text.toString().trim()
            val isOn = cbDeviceStatus.isChecked

            if (name.isEmpty() || type.isEmpty() || power.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra("device_name", name)
                putExtra("device_type", type)
                putExtra("device_power", power)
                putExtra("device_status", isOn)
            }

            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
