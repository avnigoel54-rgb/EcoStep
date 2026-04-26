package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fixed: Use the correct sign-in layout
        setContentView(R.layout.activity_sign_in)

        val btnPersonal = findViewById<Button>(R.id.btnPersonal)
        val btnOrg = findViewById<Button>(R.id.btnOrg)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etOrgId = findViewById<EditText>(R.id.etOrgId)
        val spinnerRole = findViewById<Spinner>(R.id.spinnerRole)
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)

        val roles = arrayOf("Admin", "Manager", "Viewer")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        spinnerRole.adapter = adapter

        btnOrg.setOnClickListener {
            etOrgId.visibility = View.VISIBLE
            spinnerRole.visibility = View.VISIBLE
        }

        btnPersonal.setOnClickListener {
            etOrgId.visibility = View.GONE
            spinnerRole.visibility = View.GONE
        }

        btnSignIn.setOnClickListener {
            val emailInput = etEmail.text.toString().trim()

            // Gatekeeper: Check for @ and .com
            if (emailInput.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {

                // Extract name (e.g., "ayesha" from ayesha@gmail.com)
                val nameFromEmail = emailInput.substringBefore("@")

                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("USER_NAME", nameFromEmail)
                startActivity(intent)

            } else {
                Toast.makeText(this, "Enter a proper email (e.g. name@mail.com)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
