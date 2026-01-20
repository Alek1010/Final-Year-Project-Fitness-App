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


//allows client to log bodyweight over time
//shows progress using a line chart
//compare actual log weight to trajectory
class WeightProgressActivity : AppCompatActivity() {

    //list of actual weight enteres logged by user
    //x value weeks 1-12 in this case pre defined
    //y value bodyweight in kg
    //rate of loss set to -0.5kg per week
    //future set up so either coach or clinet can adjust and chanfe these numbers
    private val actualWeights = mutableListOf<Entry>()
    //assumes one weight entry per week
    private var currentWeek = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inflate layout
        setContentView(R.layout.activity_weight_progress)

        //input fuield for bodyweight
        val weightInput = findViewById<EditText>(R.id.etWeight)
        //displays historic log entries
        val historyText = findViewById<TextView>(R.id.tvHistory)
        // line chart ui provided by MPAndroidChart
        val chart = findViewById<LineChart>(R.id.weightChart)

        setupChart(chart)

        //button click listens for new weight entry
        findViewById<Button>(R.id.btnAddWeight).setOnClickListener {
            //convert input to float, null or invalid
            val weight = weightInput.text.toString().toFloatOrNull()
            //validation
            if (weight == null) {
                Toast.makeText(this, "Enter a valid weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //new data point to the list
            //x current week
            //y entered weight
            actualWeights.add(Entry(currentWeek, weight))
            currentWeek++
            //refresh chart
            updateChart(chart)
            //formate and display date for logging history
            val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date())
            // prepare new entry for history log
            historyText.text =
                "Week ${currentWeek.toInt() - 1} ($date): $weight kg\n" + historyText.text
            //after loggin clear
            weightInput.text.clear()
        }
    }
    //configure general chart behavious and axis properties
    //show clear time frame week 1 -12
    // to do is make this editable by user
    private fun setupChart(chart: LineChart) {
        //disable desctiption at bottom
        chart.description.isEnabled = false
        //clean up axis
        chart.axisRight.isEnabled = false
        //confiugure x axis to show weeks
        chart.xAxis.apply {
            granularity = 1f// one label per week
            axisMinimum = 1f// week 1
            axisMaximum = 12f // week12 3months
            labelCount = 12
        }
    }
    //rebuild chart when new data is logged
    private fun updateChart(chart: LineChart) {

        //actual weight blue line shows users current and real logged mesurment
        val actualSet = LineDataSet(actualWeights, "Current Weight").apply {
            color = android.graphics.Color.BLUE
            setCircleColor(android.graphics.Color.BLUE)
            lineWidth = 2f
            valueTextSize = 10f
        }
        //target loss
        //red line assumes -0,5kg lost per week
        //should be changable by user future updates x
        val targetEntries = mutableListOf<Entry>()
        //start weight is first logged messured
        val startWeight = actualWeights.first().y
        val weeklyLoss = 0.5f
        //generate target value for 12 week period
        for (week in 1..12) {
            val targetWeight = startWeight - (weeklyLoss * (week -1) )
            targetEntries.add(Entry(week.toFloat(), targetWeight))
        }

        val targetSet = LineDataSet(targetEntries, "Target Weight").apply {
            color = android.graphics.Color.RED
            lineWidth = 2f
            enableDashedLine(10f, 5f, 0f)
            setDrawCircles(false)
        }
        //combine both data set and update the chart
        //invalidate forces a redrae wioth new data
        chart.data = LineData(actualSet, targetSet)
        chart.invalidate()
    }
}
