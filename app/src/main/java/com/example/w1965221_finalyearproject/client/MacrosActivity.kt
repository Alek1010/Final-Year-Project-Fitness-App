package com.example.w1965221_finalyearproject.client

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.FirebaseFunc.MacroLogUtils
import com.example.w1965221_finalyearproject.FirebaseFunc.UserUtils
import com.example.w1965221_finalyearproject.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//allows client to
//view macro targets
//save daily intake for selected date
//load intake from previous dates using calender picker
class MacrosActivity : AppCompatActivity(){
    //needed acroess multiple functions
    //delcated at class level outside on create
    //lateinit doesnt have vairable but will get one
    //xml not loaded yet so cannot do findViewbyID
    private lateinit var tvSelectedDate: TextView//selected date
    //input fields for daily macors
    private lateinit var etCalories: EditText
    private lateinit var etProtein: EditText
    private lateinit var etCarbs: EditText
    private lateinit var etFats: EditText
    private lateinit var etWater: EditText
    //summart box showing what was saved/ loaded
    private lateinit var tvSummary: TextView

    //  currently selected date for viewing/saving logs
    private val selectedCalendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //inflate the xml
        //xml ui kotlin does behavour
        setContentView(R.layout.activity_macros)

        val tvTargets = findViewById<TextView>(R.id.tvMacroTargets)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)

        etCalories = findViewById(R.id.etCalories)
        etProtein = findViewById(R.id.etProtein)
        etCarbs = findViewById(R.id.etCarbs)
        etFats = findViewById(R.id.etFats)
        etWater = findViewById(R.id.etWater)

        tvSummary = findViewById(R.id.tvSummary)

        val btnSaveMacro = findViewById<Button>(R.id.btnSaveMacros)
        val btnPickDate = findViewById<Button>(R.id.btnPickDate)

        //load exisit macros target from firebase
        UserUtils.loadClientOverview(tvTargets)

        //show todays date
        updateDisplayedDate()

        //load todays log if one already exists
        loadMacrosForSelectedDate()

        //save todays macros  if alreadt exist
        btnSaveMacro.setOnClickListener{
            saveMacroForSelectedDate()
        }
        //open calider date picker
        btnPickDate.setOnClickListener{
            openDatePicker()
        }

    }

    //formats for the selected date in two diffrent
    //ui formate 11 mar 2025
    private fun getDisplayDate(): String{
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(selectedCalendar.time)
    }
    //formate date as firebase document id
    //e.g 2025-03-11
    private fun getDateId(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(selectedCalendar.time)
    }
    //update screen date
    private fun updateDisplayedDate() {
        tvSelectedDate.text = "Date: ${getDisplayDate()}"
    }

    //open androids calender date picker
    //after user selectes date app loads that days macor log
    private fun openDatePicker(){
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val day = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this,{_,selectedYear, selectedMonth, selectedDay ->
            selectedCalendar.set(selectedYear,selectedMonth,selectedDay)
            updateDisplayedDate()
            loadMacrosForSelectedDate()
        },year,month,day).show()
    }

    //saves macros for the currently selected date
    //if the same data is saved again Firestore updates same document
    private fun saveMacroForSelectedDate(){
        val calories = etCalories.text.toString().toIntOrNull()
        val protein = etProtein.text.toString().toIntOrNull()
        val carbs = etCarbs.text.toString().toIntOrNull()
        val fats = etFats.text.toString().toIntOrNull()
        val water = etWater.text.toString().toDoubleOrNull()

        if (calories == null || protein == null || carbs == null || fats == null || water == null) {
            Toast.makeText(this, "Enter valid values for all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val dateId = getDateId()

        MacroLogUtils.saveMacroLog(
            dateId = dateId,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fats = fats,
            water = water,
            onSuccess = {
                tvSummary.text =
                            "Logged Intake\n" +
                            "Date: ${getDisplayDate()}\n" +
                            "Calories: $calories kcal\n" +
                            "Protein: $protein g\n" +
                            "Carbs: $carbs g\n" +
                            "Fats: $fats g\n" +
                            "Water: $water L"

                Toast.makeText(this, "Macros saved for ${getDisplayDate()}", Toast.LENGTH_SHORT).show()
            },
            onFailure = {e ->
                Toast.makeText(this,"save fialed: ${e.message}",Toast.LENGTH_SHORT).show()
            }
        )
    }

    // load the macros log for the currently selected date
    //puts it into the input box if no input on that day field
    //are cleared

    private fun loadMacrosForSelectedDate(){
        val dateId = getDateId()

        MacroLogUtils.loadMacroLog(
            dateId = dateId,
            onSuccess = {data ->
                if (data == null){
                    clearInputs()
                    tvSummary.text = "no log saved for ${getDisplayDate()}"
                    return@loadMacroLog
                }
                etCalories.setText((data["calories"] as? Long)?.toInt()?.toString() ?: "")
                etProtein.setText((data["protein"] as? Long)?.toInt()?.toString() ?: "")
                etCarbs.setText((data["carbs"] as? Long)?.toInt()?.toString() ?: "")
                etFats.setText((data["fats"] as? Long)?.toInt()?.toString() ?: "")
                etWater.setText((data["water"] as? Double)?.toString() ?: "")

                tvSummary.text =
                    "Loaded Intake\n" +
                            "Date: ${getDisplayDate()}\n" +
                            "Calories: ${etCalories.text} kcal\n" +
                            "Protein: ${etProtein.text} g\n" +
                            "Carbs: ${etCarbs.text} g\n" +
                            "Fats: ${etFats.text} g\n" +
                            "Water: ${etWater.text} L"
            },
            onFailure = { e->
                Toast.makeText(this,"load Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun clearInputs(){
        etCalories.text.clear()
        etProtein.text.clear()
        etCarbs.text.clear()
        etFats.text.clear()
        etWater.text.clear()
    }


}