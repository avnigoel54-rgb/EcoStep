package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class QrSetupActivity : AppCompatActivity() {

    private val qrScannerLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            handleQrResult(result.contents)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_setup)

        val etQrCode = findViewById<EditText>(R.id.etQrCode)
        val btnAddQrDevice = findViewById<Button>(R.id.btnAddQrDevice)
        val btnBackFromQr = findViewById<Button>(R.id.btnBackFromQr)
        val btnLaunchScanner = findViewById<Button>(R.id.btnLaunchScanner)

        btnBackFromQr.setOnClickListener {
            finish()
        }

        btnLaunchScanner.setOnClickListener {
            val options = ScanOptions()
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            options.setPrompt("Scan a device QR code")
            options.setCameraId(0)
            options.setBeepEnabled(true)
            options.setBarcodeImageEnabled(true)
            options.setOrientationLocked(false)
            qrScannerLauncher.launch(options)
        }

        btnAddQrDevice.setOnClickListener {
            val qrText = etQrCode.text.toString().trim()
            if (qrText.isNotEmpty()) {
                handleQrResult(qrText)
            } else {
                Toast.makeText(this, "Please enter setup text or scan a code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleQrResult(qrText: String) {
        // Expected format: name=Smart Fan;type=Appliance;power=1.2 kWh;status=on
        try {
            val map = qrText.split(";").associate {
                val split = it.split("=")
                split[0].trim() to split[1].trim()
            }

            val name = map["name"] ?: throw Exception("Missing name")
            val type = map["type"] ?: "Device"
            val power = map["power"] ?: "1.0 kWh"
            val status = map["status"] ?: "on"
            val isOn = status.lowercase() != "off"

            val resultIntent = Intent().apply {
                putExtra("device_name", name)
                putExtra("device_type", type)
                putExtra("device_power", power)
                putExtra("device_status", isOn)
            }

            setResult(RESULT_OK, resultIntent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid QR Format", Toast.LENGTH_LONG).show()
        }
    }
}
