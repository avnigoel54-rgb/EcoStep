package com.example.ecotrack

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.database.*

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private lateinit var heatmapGrid: GridLayout
    private lateinit var heatmapScroll: ScrollView
    private lateinit var database: DatabaseReference
    private var currentMonth = "March"
    private val monthData = mutableMapOf<Int, Float>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        lineChart = findViewById(R.id.lineChart)
        heatmapGrid = findViewById(R.id.heatmapGrid)
        heatmapScroll = findViewById(R.id.heatmapScroll)
        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)
        val btnThemeToggle = findViewById<ImageButton>(R.id.btnThemeToggle)

        database = FirebaseDatabase.getInstance().reference.child("analytics")

        setupMonthButtons()
        setupBottomNav()

        btnThemeToggle.setOnClickListener {
            val mode = if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
                AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == R.id.btnLineChart) {
                    lineChart.visibility = View.VISIBLE
                    heatmapScroll.visibility = View.GONE
                    updateLineChart()
                } else {
                    lineChart.visibility = View.GONE
                    heatmapScroll.visibility = View.VISIBLE
                    updateHeatmap()
                }
            }
        }

        fetchData(currentMonth)
    }

    private fun setupMonthButtons() {
        val months = listOf("January", "February", "March", "April", "May", "June")
        val ids = listOf(R.id.btnJan, R.id.btnFeb, R.id.btnMar, R.id.btnApr, R.id.btnMay, R.id.btnJun)

        ids.forEachIndexed { index, id ->
            findViewById<Button>(id).setOnClickListener {
                currentMonth = months[index]
                fetchData(currentMonth)
                Toast.makeText(this, "Loading $currentMonth...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchData(month: String) {
        database.child(month).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                monthData.clear()
                for (daySnapshot in snapshot.children) {
                    val day = daySnapshot.key?.toIntOrNull() ?: continue
                    val value = daySnapshot.getValue(Float::class.java) ?: 0f
                    monthData[day] = value
                }
                updateLineChart()
                updateHeatmap()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AnalyticsActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateLineChart() {
        val entries = monthData.map { Entry(it.key.toFloat(), it.value) }.sortedBy { it.x }
        val dataSet = LineDataSet(entries, "Energy Usage (kWh)").apply {
            color = ContextCompat.getColor(this@AnalyticsActivity, R.color.eco_green)
            setCircleColor(Color.WHITE)
            lineWidth = 2f
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextColor = Color.GRAY
            valueTextSize = 10f
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(this@AnalyticsActivity, R.color.eco_green)
        }

        lineChart.data = LineData(dataSet)
        lineChart.description.text = "$currentMonth Usage"
        lineChart.animateX(1000)
        lineChart.invalidate()
    }

    private fun updateHeatmap() {
        heatmapGrid.removeAllViews()
        val maxUsage = monthData.values.maxOrNull() ?: 1f

        for (day in 1..30) {
            val usage = monthData[day] ?: 0f
            val intensity = (usage / maxUsage)
            
            val box = TextView(this).apply {
                val size = 120
                layoutParams = GridLayout.LayoutParams().apply {
                    width = size
                    height = size
                    setMargins(8, 8, 8, 8)
                }
                text = day.toString()
                gravity = Gravity.CENTER
                textSize = 12f
                setTextColor(if (intensity > 0.5) Color.WHITE else Color.BLACK)
                
                // Color intensity logic: Light green to Dark green
                val alpha = (intensity * 255).toInt().coerceIn(50, 255)
                setBackgroundColor(Color.argb(alpha, 88, 179, 141)) // Based on eco_green #58B38D
            }
            heatmapGrid.addView(box)
        }
    }

    private fun setupBottomNav() {
        findViewById<TextView>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
        findViewById<TextView>(R.id.navDevices).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<TextView>(R.id.navTips).setOnClickListener {
            startActivity(Intent(this, TipsActivity::class.java))
            finish()
        }
    }
}
