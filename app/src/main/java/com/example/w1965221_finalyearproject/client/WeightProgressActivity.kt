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
import com.example.w1965221_finalyearproject.FirebaseFunc.WeightGoalUtils
import com.example.w1965221_finalyearproject.calculations.WeightGoalCalculator


//allows client to log bodyweight over time
//shows progress using a line chart
//compare actual log weight to trajectory
class WeightProgressActivity : AppCompatActivity() {
    //chart uses (x,y) x week number
    //y bodyweight in kg
    // users actual logged weight  stores real body weight entries
    //user logs
    private val actualWeights = mutableListOf<Entry>()

    //assumes one weight entry per week
    private var currentWeek = 1f

    //load from firebase during calibration
    private var startWeightKg =0.0
    private var weeklyRateKg = 0.0
    private var durationWeeks = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inflate layout
        setContentView(R.layout.activity_weight_progress)
        //ui references
        val tvCurrentWeight = findViewById<TextView>(R.id.tvCurrentWeight)
        val tvRate = findViewById<TextView>(R.id.tvRate)
        val tvGoalWeight = findViewById<TextView>(R.id.tvGoalWeight)
        val durationInput = findViewById<EditText>(R.id.etDurationWeeks)
        val weightInput = findViewById<EditText>(R.id.etWeight)
        val historyText = findViewById<TextView>(R.id.tvHistory)
        val chart = findViewById<LineChart>(R.id.weightChart)

        val btnSetGoal = findViewById<Button>(R.id.btnSetGoal)
        val btnAddWeight = findViewById<Button>(R.id.btnAddWeight)

        //configure chart when screen opens empty plot
        setupChart(chart)

        //load the starting wright and weekly rate from fire base
        WeightGoalUtils.loadWeightGoalProfile(
            onSuccess = {profile ->
                if (profile == null){
                    Toast.makeText(this,"no goal progfile found",Toast.LENGTH_SHORT).show()
                    return@loadWeightGoalProfile
                }
                //store firebase values locallu inside the activity
                startWeightKg = profile.bodyWeightKg
                weeklyRateKg = profile.weeklyRateKg
                //show in ui
                tvCurrentWeight.text = "current weight: ${startWeightKg}kg"
                tvRate.text = "Rate: ${weeklyRateKg} kg/week"
                //add starting point to chart
                actualWeights.clear()
                actualWeights.add(Entry(1f,startWeightKg.toFloat()))
                currentWeek = 2f
                updateChart(chart)
            },
            onFailure = {e->
                Toast.makeText(this,"Failed to load profile: ${e.message}",Toast.LENGTH_LONG).show()
            }
        )

        //user enters number of weeks it should run
        btnSetGoal.setOnClickListener{
            val weeks = durationInput.text.toString().toIntOrNull()

            if (weeks == null || weeks <= 0) {
                Toast.makeText(this,"enter a Valid number of weeks ",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            durationWeeks = weeks
            //use calculator to wokrout final goal weight
            val result = WeightGoalCalculator.calculateGoalWeight(
                startWeightKg = startWeightKg,
                weeklyRateKg = weeklyRateKg,
                durationWeeks = durationWeeks
            )

            tvGoalWeight.text = "Goal weight: ${result.finalGoalWeightKg}kg"
            //rebuild chart so red line matches new duration
            updateChart(chart)
        }

        // user logs new actual weight
        btnAddWeight.setOnClickListener{
            val weight = weightInput.text.toString().toFloatOrNull()
            if(weight == null){
                Toast.makeText(this,"enter a valid weight",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //adds real weight to the weight list
            actualWeights.add(Entry(currentWeek,weight))
            currentWeek++//move to next week

            updateChart(chart)
            //add log next to histroy box
            val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            historyText.text =
                "Week ${currentWeek.toInt() - 1} ($date): $weight kg\n" + historyText.text
            //clear input box after saving
            weightInput.text.clear()

        }
    }
    //chart set up
    //configuers line chart
    private fun setupChart(chart: LineChart) {
        chart.description.isEnabled = false//auto ads small description disabled
        chart.axisRight.isEnabled = false // disable right side y axis
        chart.xAxis.granularity = 1f //granularity show axis in whole weeks
    }
    //rebuild chart using actual loged weight
    //target weight line based on firebase rate+ chose duration
    private fun updateChart(chart: LineChart) {
        //bleu actual weight put by user
        val actualSet = LineDataSet(actualWeights,"Current Weight").apply {
            color = android.graphics.Color.BLUE//make line blue
            setCircleColor(android.graphics.Color.BLUE)//blue color the circle markers
            lineWidth = 2f // thicness
            valueTextSize = 10f // size of nuber labels at each point
        }

        //red line target weight done
        //mathmatically
        //target = start+ weeklyrate*week-1
        val targetEntries = mutableListOf<Entry>()

        for (week in 1..durationWeeks){
            val targetWeight = startWeightKg +(weeklyRateKg*(week-1))
            targetEntries.add(Entry(week.toFloat(), targetWeight.toFloat()))
        }
        //make target red
        val targetSet = LineDataSet(targetEntries,"Target Weight").apply {
            color = android.graphics.Color.RED
            lineWidth = 2f
            enableDashedLine(10f,5f,0f)
            setDrawCircles(false)
        }

        // update axis to match chosen duration
        chart.xAxis.axisMinimum = 1f
        chart.xAxis.axisMaximum = durationWeeks.toFloat()
        chart.xAxis.labelCount = durationWeeks
        chart.data = LineData(actualSet,targetSet)
        //redraw chart using the new data
        chart.invalidate()
    }
}
