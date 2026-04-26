package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.myapplication.R
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class QrSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_setup)

        val etQrCode = findViewById<EditText>(R.id.etQrCode)
        val btnAddQrDevice = findViewById<Button>(R.id.btnAddQrDevice)
        val btnBackFromQr = findViewById<Button>(R.id.btnBackFromQr)

        btnBackFromQr.setOnClickListener {
            finish()
        }

        btnAddQrDevice.setOnClickListener {
            val qrText = etQrCode.text.toString().trim()

            if (qrText.isEmpty()) {
                Toast.makeText(this, "Please enter a QR/setup code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val parts = qrText.split("|")

            val name = parts.getOrNull(0)?.ifBlank { "Scanned Device" } ?: "Scanned Device"
            val type = parts.getOrNull(1)?.ifBlank { "Matter Device" } ?: "Matter Device"
            val power = parts.getOrNull(2)?.ifBlank { "1.0 kWh" } ?: "1.0 kWh"
            val statusText = parts.getOrNull(3)?.lowercase() ?: "on"
            val isOn = statusText != "off"

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

