package com.example.w1965221_finalyearproject.client

import android.graphics.Color
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
import com.example.w1965221_finalyearproject.FirebaseFunc.WeightLogUtils
import com.example.w1965221_finalyearproject.calculations.WeightGoalCalculator
import com.example.w1965221_finalyearproject.client.DailyWeightLog
import com.github.mikephil.charting.components.XAxis
import kotlin.math.roundToInt


//allows client to log bodyweight over time
//shows progress using a line chart
//compare actual log weight to trajectory
class WeightProgressActivity : AppCompatActivity() {


    //stores are daily werighIn while app is open
    //right now only stored in memory
    private val dailyLogs = mutableListOf<DailyWeightLog>()


    //load from firebase during calibration
    private var startWeightKg =0.0
    private var weeklyRateKg = 0.0
    private var durationWeeks = 12
    //vairable passed only when coach opens client weight progress
    private var viewClientUid: String? = null

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

        //reading passed CLientUId if opened by coach contains UID
        // else opened by client == null
        viewClientUid = intent.getStringExtra("client_uid")


        //configure chart when screen opens empty plot
        setupChart(chart)

        // checks if viewClientuID EXITST if it does
        //load the clients progress for the coach
        //else works normally in client view
        //all pulled from firebase
        if (viewClientUid != null) {
            WeightGoalUtils.loadWeightGoalProfileForUser(
                userUid = viewClientUid!!,
                onSuccess = { profile ->
                    if (profile == null) {
                        Toast.makeText(this, "No goal profile found", Toast.LENGTH_SHORT).show()
                        return@loadWeightGoalProfileForUser
                    }

                    startWeightKg = profile.bodyWeightKg
                    weeklyRateKg = profile.weeklyRateKg

                    tvCurrentWeight.text = "Current weight: ${format1dp(startWeightKg)} kg"
                    tvRate.text = "Rate: ${format1dp(weeklyRateKg)} kg/week"

                    refreshWeightLogsForViewedUser(
                        chart = chart,
                        historyText = historyText,
                        tvCurrentWeight = tvCurrentWeight
                    )
                },
                onFailure = { e ->
                    Toast.makeText(
                        this,
                        "Failed to load profile: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        } else {
            WeightGoalUtils.loadWeightGoalProfile(
                onSuccess = { profile ->
                    if (profile == null) {
                        Toast.makeText(this, "No goal profile found", Toast.LENGTH_SHORT).show()
                        return@loadWeightGoalProfile
                    }

                    startWeightKg = profile.bodyWeightKg
                    weeklyRateKg = profile.weeklyRateKg

                    tvCurrentWeight.text = "Current weight: ${format1dp(startWeightKg)} kg"
                    tvRate.text = "Rate: ${format1dp(weeklyRateKg)} kg/week"

                    refreshWeightLogs(
                        chart = chart,
                        historyText = historyText,
                        tvCurrentWeight = tvCurrentWeight
                    )
                },
                onFailure = { e ->
                    Toast.makeText(
                        this,
                        "Failed to load profile: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
        //user enters number of weeks it should run
        btnSetGoal.setOnClickListener {
            val weeks = durationInput.text.toString().toIntOrNull()

            if (weeks == null || weeks <= 0) {
                Toast.makeText(this, "Enter a valid number of weeks", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            durationWeeks = weeks

            val result = WeightGoalCalculator.calculateGoalWeight(
                startWeightKg = startWeightKg,
                weeklyRateKg = weeklyRateKg,
                durationWeeks = durationWeeks
            )

            tvGoalWeight.text = "Goal weight: ${format1dp(result.finalGoalWeightKg)} kg"

            WeightLogUtils.saveWeightProgressSettings(
                durationWeeks = durationWeeks,
                goalWeightKg = result.finalGoalWeightKg,
                onSuccess = {
                    Toast.makeText(this, "Goal settings saved", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Toast.makeText(
                        this,
                        "Failed to save goal settings: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )

            updateChart(chart)
        }

        // user logs new daily weight
        btnAddWeight.setOnClickListener {
            val weight = weightInput.text.toString().toDoubleOrNull()

            if (weight == null) {
                Toast.makeText(this, "Enter a valid weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val todayStorageDate = getTodayStorageDate()

            WeightLogUtils.saveDailyWeightLog(
                date = todayStorageDate,
                weightKg = weight,
                onSuccess = {
                    Toast.makeText(this, "Today's weight saved", Toast.LENGTH_SHORT).show()

                    weightInput.text.clear()

                    refreshWeightLogs(
                        chart = chart,
                        historyText = historyText,
                        tvCurrentWeight = tvCurrentWeight
                    )
                },
                onFailure = { e ->
                    Toast.makeText(
                        this,
                        "Failed to save weight: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }
    //loads the data for the coaches client not for logged in
    //user
    //if the ciewclientUid in use it will pull the data
    //for the correct client
    private fun refreshWeightLogsForViewedUser(
        chart: LineChart,
        historyText: TextView,
        tvCurrentWeight: TextView
    ) {
        val targetUid = viewClientUid ?: return

        WeightLogUtils.loadDailyWeightLogsForUser(
            userUid = targetUid,
            onSuccess = { logs ->
                dailyLogs.clear()
                dailyLogs.addAll(logs)

                if (dailyLogs.isEmpty()) {
                    tvCurrentWeight.text = "Current weight: ${format1dp(startWeightKg)} kg"
                } else {
                    val latestLog = dailyLogs.maxByOrNull { it.date }
                    if (latestLog != null) {
                        tvCurrentWeight.text =
                            "Current weight: ${format1dp(latestLog.weightKg)} kg"
                    }
                }

                rebuildHistory(historyText)
                updateChart(chart)
            },
            onFailure = { e ->
                Toast.makeText(
                    this,
                    "Failed to load weight logs: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    //roloads all daily logs from and refreshs ui
    private fun refreshWeightLogs(
        chart: LineChart,
        historyText: TextView,
        tvCurrentWeight: TextView
    ) {
        WeightLogUtils.loadDailyWeightLogs(
            onSuccess = { logs ->
                dailyLogs.clear()
                dailyLogs.addAll(logs)

                if (dailyLogs.isEmpty()) {
                    tvCurrentWeight.text = "Current weight: ${format1dp(startWeightKg)} kg"
                } else {
                    val latestLog = dailyLogs.maxByOrNull { it.date }
                    if (latestLog != null) {
                        tvCurrentWeight.text =
                            "Current weight: ${format1dp(latestLog.weightKg)} kg"
                    }
                }

                rebuildHistory(historyText)
                updateChart(chart)
            },
            onFailure = { e ->
                Toast.makeText(
                    this,
                    "Failed to load weight logs: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    //chart set up
    //configuers line chart
    private fun setupChart(chart: LineChart) {
        chart.description.isEnabled = false//auto ads small description disabled
        chart.axisRight.isEnabled = false // disable right side y axis
        chart.setNoDataText("no weight data yet")//message if no data
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM// put xaxis at bottom
        chart.xAxis.granularity = 1f //granularity show axis in whole weeks
        chart.xAxis.axisMinimum = 1f //start from week 1
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
    private fun rebuildHistory(historyText: TextView) {
        val sortedLogs = dailyLogs.sortedByDescending { it.date }
        val history = StringBuilder()

        for (log in sortedLogs) {
            val displayDate = storageDateToDisplayDate(log.date)
            history.append("$displayDate: ${format1dp(log.weightKg)} kg\n")
        }

        historyText.text = history.toString()
    }

    //return todays date as a string
    private fun getTodayStorageDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    //converts stored data formate to user friendly display formate
    private fun storageDateToDisplayDate(storageDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val parsedDate = inputFormat.parse(storageDate)
            if (parsedDate != null) {
                outputFormat.format(parsedDate)
            } else {
                storageDate
            }
        } catch (e: Exception) {
            storageDate
        }
    }

    //helper function round a num to 1 decimal
    //e.g 56.7343321 = 56.7
    private fun format1dp(value:Double): String{
        return ((value * 10).roundToInt() / 10.0).toString()
    }


}
