package com.example.ecotrack

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private var isSignUpMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val btnPersonal = findViewById<Button>(R.id.btnPersonal)
        val btnOrg = findViewById<Button>(R.id.btnOrg)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etOrgId = findViewById<EditText>(R.id.etOrgId)
        val tilOrgId = findViewById<TextInputLayout>(R.id.tilOrgId)
        val spinnerRole = findViewById<Spinner>(R.id.spinnerRole)
        val flRole = findViewById<FrameLayout>(R.id.flRole)
        val btnSignIn = findViewById<Button>(R.id.btnMain)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvToggleMode = findViewById<TextView>(R.id.tvToggleMode)
        val tilName = findViewById<TextInputLayout>(R.id.tilName)

        val roles = arrayOf("Admin", "Manager", "Viewer")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        spinnerRole.adapter = adapter

        btnOrg.setOnClickListener {
            tilOrgId.visibility = View.VISIBLE
            flRole.visibility = View.VISIBLE
        }

        btnPersonal.setOnClickListener {
            tilOrgId.visibility = View.GONE
            flRole.visibility = View.GONE
        }

        tvToggleMode.setOnClickListener {
            isSignUpMode = !isSignUpMode
            if (isSignUpMode) {
                tvTitle.text = "Create Account"
                btnSignIn.text = "Sign Up"
                tvToggleMode.text = "Already have an account? Sign In"
                tilName.visibility = View.VISIBLE
            } else {
                tvTitle.text = "Welcome Back"
                btnSignIn.text = "Sign In"
                tvToggleMode.text = "Don't have an account? Sign Up"
                tilName.visibility = View.GONE
            }
        }

        btnSignIn.setOnClickListener {
            val emailInput = etEmail.text.toString().trim()

            if (emailInput.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                val nameFromEmail = emailInput.substringBefore("@")
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("USER_NAME", nameFromEmail)
                startActivity(intent)
                finish() // Close login activity so back button doesn't return here
            } else {
                Toast.makeText(this, "Enter a proper email (e.g. name@mail.com)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
