package com.example.w1965221_finalyearproject.client

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.Modifier
import com.example.w1965221_finalyearproject.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.*
import com.example.w1965221_finalyearproject.FirebaseFunc.WeightGoalUtils
import com.example.w1965221_finalyearproject.calculations.WeightGoalCalculator
import com.github.mikephil.charting.components.XAxis
import kotlin.math.roundToInt


//allows client to log bodyweight over time
//shows progress using a line chart
//compare actual log weight to trajectory
class WeightProgressActivity : AppCompatActivity() {

    //this class represetns one daily weight log
    //e.g 07/04/2026
    //weightKg = 56.4f
    //weight is logged daily and averge weight for that week is plotted
    //on the graph
    data class DailyWeightLog(
        val date: String,
        val weightKg: Float
    )
    //stores are daily werighIn while app is open
    //right now only stored in memory
    private val dailyLogs = mutableListOf<DailyWeightLog>()

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
                dailyLogs.clear()
                //starting weight as first entry
                val today = getTodayString()
                dailyLogs.add(
                    DailyWeightLog(
                        date = today,
                        weightKg = startWeightKg.toFloat()
                    )
                )
                //refresh visibility to saved daily weights
                rebuildHistory(historyText)

                //redraw graph
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

        // user logs new daily weight
        btnAddWeight.setOnClickListener{
            //read todays enterd weight from the input box
            val weight = weightInput.text.toString().toFloatOrNull()
            //validate
            if(weight == null){
                Toast.makeText(this,"enter a valid weight",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //get todays date as a string
            val today = getTodayString()

            //check if there is alreafy an entry for today
            //if so replace existing one with new one
            val existingIndex = dailyLogs.indexOfFirst { it.date == today }

            if (existingIndex != -1){
                //update todays existing entry
                dailyLogs[existingIndex] = DailyWeightLog(today, weight)
                Toast.makeText(this,"today's weight updated", Toast.LENGTH_SHORT).show()
            }else{
                //add brand new daily entry
                dailyLogs.add(DailyWeightLog(today,weight))
                Toast.makeText(this,"todays weight added", Toast.LENGTH_SHORT).show()
            }

            // update current weight text to the latest value user entered
            tvCurrentWeight.text = "Current weight: ${format1dp(weight.toDouble())} kg"

            //rebuild the visioble history list
            rebuildHistory(historyText)

            updateChart(chart)
            weightInput.text.clear()

        }
    }
    //chart set up
    //configuers line chart
    private fun setupChart(chart: LineChart) {
        chart.description.isEnabled = false//auto ads small description disabled
        chart.axisRight.isEnabled = false // disable right side y axis
        chart.setNoDataText("no weight data yet")//message if no data
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM// put xaxis at bottom
        chart.xAxis.granularity = 1f //granularity show axis in whole weeks
        chart.xAxis.axisMaximum = 1f //start from week 1
    }
    //rebuild chart using actual loged weight
    //target weight line based on firebase rate+ chose duration
    //converts daily weighins into weekly averages
    private fun updateChart(chart: LineChart) {
        val weeklyAverageEntries = buildWeeklyAverageEntries()

        //create blue line for actual progress
        val averageSet = LineDataSet(weeklyAverageEntries,"Weekly Average").apply {
            color = Color.BLUE
            setCircleColor(Color.RED)
            lineWidth = 2f
            valueTextSize = 10f
        }

        //create red target line
        val targetEntry = mutableListOf<Entry>()
        for (week in 1..durationWeeks){
            val targetWeight = startWeightKg + (weeklyRateKg * (week - 1))
            targetEntry.add(Entry(week.toFloat(),targetWeight.toFloat()))
        }

        //create dataset for target line
        val targetSet = LineDataSet(targetEntry,"Target Weight").apply {
            color = Color.RED
            lineWidth = 2f

        }

        //control xaxis range
        chart.xAxis.axisMaximum = durationWeeks.toFloat()
        chart.xAxis.labelCount = durationWeeks

        //put both onto the chart and redraw
        chart.data = LineData(averageSet,targetSet)
        chart.invalidate()

    }

    //convet daily logs into weekly average logs
    //7 entries == one week not using calebndar
    private fun buildWeeklyAverageEntries():MutableList<Entry>{
        val entires = mutableListOf<Entry>()
        //if mo daily logs return empty list
        if (dailyLogs.isEmpty()) return entires

        //sort log by date so weekly grouping happens in correct order
        //later when saving to firebase time stamp should be used
        val sortedLogs = dailyLogs.sortedBy { it.date }
        var weekNumber = 1
        var index = 0

        //loop through logs in chunks of 7
        while( index<sortedLogs.size){
            val endIndex = minOf(index+7,sortedLogs.size)

            val weekLogs = sortedLogs.subList(index,endIndex)

            val average = weekLogs.map { it.weightKg.toDouble() }.average()

            entires.add(Entry(weekNumber.toFloat(),average.toFloat()))

            weekNumber++

            index+=7
        }

        return entires
    }


    //updates the text history shown on screen
    //e.g 07/04/2026: 56.4
    //newest shown first
    private fun rebuildHistory(historyText: TextView){
        val sortedLogs = dailyLogs.sortedByDescending { it.date }
        val history = StringBuilder()
        for(log in sortedLogs){
            history.append("${log.date}: ${format1dp(log.weightKg.toDouble())} kg\n")
        }
        historyText.text = history.toString()
    }

    //return todays date as a string
    private fun getTodayString():String{
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    //helper function round a num to 1 decimal
    //e.g 56.7343321 = 56.7
    private fun format1dp(value:Double): String{
        return ((value * 10).roundToInt() / 10.0).toString()
    }

    
}
