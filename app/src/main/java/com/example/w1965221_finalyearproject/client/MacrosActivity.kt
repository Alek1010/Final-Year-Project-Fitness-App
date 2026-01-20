package com.example.w1965221_finalyearproject.client

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R

//allows client to log daily intake
class MacrosActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inflate the xml
        //xml ui kotlin does behavour
        setContentView(R.layout.activity_macros)

        val caloriesInput = findViewById<EditText>(R.id.etCalories)
        val proteinInput = findViewById<EditText>(R.id.etProtein)
        val carbsInput = findViewById<EditText>(R.id.etCarbs)
        val fatsInput = findViewById<EditText>(R.id.etFats)
        val waterInput = findViewById<EditText>(R.id.etWater)
        val summaryText = findViewById<TextView>(R.id.tvSummary)

        findViewById<Button>(R.id.btnSaveMacros).setOnClickListener {

            val calories = caloriesInput.text.toString()
            val protein = proteinInput.text.toString()
            val carbs = carbsInput.text.toString()
            val fats = fatsInput.text.toString()
            val water = waterInput.text.toString()

            //text view to display summary of loggied intake
            summaryText.text =
                "Logged Intake\nCalories: $calories kcal\nProtein: $protein g\nCarbs: $carbs g\nFats: $fats g" +
                        "Water: $water L"

            Toast.makeText(this, "Macros logged", Toast.LENGTH_SHORT).show()
        }
    }

}