package com.example.w1965221_finalyearproject.client

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.FirebaseFunc.UserUtils
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
        val targetText = findViewById<TextView>(R.id.tvMacroTargets)

        UserUtils.loadClientOverview(targetText)

        findViewById<Button>(R.id.btnSaveMacros).setOnClickListener {

            val calories = caloriesInput.text.toString()
            val protein = proteinInput.text.toString()
            val carbs = carbsInput.text.toString()
            val fats = fatsInput.text.toString()
            val water = waterInput.text.toString()



            Toast.makeText(this, "Macros logged", Toast.LENGTH_SHORT).show()
        }
    }

}