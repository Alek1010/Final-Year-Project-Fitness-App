package com.example.w1965221_finalyearproject.client

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.*

class WeightProgressActivity : AppCompatActivity() {


    private val actualWeights = mutableListOf<Entry>()
    private var currentWeek = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_progress)

        val weightInput = findViewById<EditText>(R.id.etWeight)
        val historyText = findViewById<TextView>(R.id.tvHistory)
        val chart = findViewById<LineChart>(R.id.weightChart)

        setupChart(chart)

        findViewById<Button>(R.id.btnAddWeight).setOnClickListener {

            val weight = weightInput.text.toString().toFloatOrNull()

            if (weight == null) {
                Toast.makeText(this, "Enter a valid weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            actualWeights.add(Entry(currentWeek, weight))
            currentWeek++

            updateChart(chart)

            val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date())

            historyText.text =
                "Week ${currentWeek.toInt() - 1} ($date): $weight kg\n" + historyText.text

            weightInput.text.clear()
        }
    }

    private fun setupChart(chart: LineChart) {
        chart.description.isEnabled = false
        chart.axisRight.isEnabled = false

        chart.xAxis.apply {
            granularity = 1f
            axisMinimum = 1f
            axisMaximum = 12f
            labelCount = 12
        }
    }

    private fun updateChart(chart: LineChart) {

        val actualSet = LineDataSet(actualWeights, "Current Weight").apply {
            color = android.graphics.Color.BLUE
            setCircleColor(android.graphics.Color.BLUE)
            lineWidth = 2f
            valueTextSize = 10f
        }

        val targetEntries = mutableListOf<Entry>()
        val startWeight = actualWeights.first().y
        val weeklyLoss = 0.5f

        for (week in 1..12) {
            val targetWeight = startWeight - (weeklyLoss * week)
            targetEntries.add(Entry(week.toFloat(), targetWeight))
        }

        val targetSet = LineDataSet(targetEntries, "Target Weight").apply {
            color = android.graphics.Color.RED
            lineWidth = 2f
            enableDashedLine(10f, 5f, 0f)
            setDrawCircles(false)
        }

        chart.data = LineData(actualSet, targetSet)
        chart.invalidate()
    }
}
