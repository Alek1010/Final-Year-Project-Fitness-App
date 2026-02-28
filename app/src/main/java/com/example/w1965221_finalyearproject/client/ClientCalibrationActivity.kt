package com.example.w1965221_finalyearproject.client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import android.text.Editable
import android.widget.EditText
import android.text.TextWatcher




//client calibration
//collects data for the client to workout marcos
//and displays it to the client dashboard
class ClientCalibrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_calibration)

        val finishbutton = findViewById<Button>(R.id.btnFinishSetup)
        val skipText = findViewById<TextView>(R.id.tvSkip)
        val rgTargetMode = findViewById<RadioGroup>(R.id.rgTargetMode)
        val layoutManual = findViewById<android.view.View>(R.id.layoutManual)
        val layoutAuto = findViewById<android.view.View>(R.id.layoutAuto)

        val rgGaol = findViewById<RadioGroup>(R.id.rgGoal)
        val layoutRate = findViewById<android.view.View>(R.id.layoutRate)

        val proteinInput = findViewById<EditText>(R.id.etTargetProtein)
        val carbsInput = findViewById<EditText>(R.id.etTargetCarbs)
        val fatsInput = findViewById<EditText>(R.id.etTargetFats)
        val caloriesText = findViewById<TextView>(R.id.tvCalculatedCalories)

        //helper safley read frams from edittext if input ==
        //50g it wont crash
        fun getInt(editText: EditText):Int{
            return editText.text.toString().toIntOrNull() ?: 0
        }

        //recalculate caloreis and update ui
        fun updateCalories(){
            val protein = getInt(proteinInput)
            val carbs = getInt(carbsInput)
            val fats = getInt(fatsInput)

            val calories = (protein*4)+(carbs*4)+(fats*9)
            caloriesText.text = "$calories kcal"
        }

        //one shared watcher fro all three feilds
        val watcher = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateCalories()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        //attch watcher to all marco inputs
        proteinInput.addTextChangedListener(watcher)
        carbsInput.addTextChangedListener(watcher)
        fatsInput.addTextChangedListener(watcher)

        //set initaial value to 0kcal
        updateCalories()

        //switch between manaula and auto selections
        rgTargetMode.setOnCheckedChangeListener{ _,checkedId ->
            val isManual = checkedId == R.id.rbManual
            layoutManual.visibility = if (isManual) android.view.View.VISIBLE else android.view.View.GONE
            layoutAuto.visibility = if (isManual) android.view.View.GONE else android.view.View.VISIBLE

        }

        //show rate option only when gain or lose weight is selected
        rgGaol.setOnCheckedChangeListener{_, checkedId ->
            val showRate = (checkedId == R.id.rbGain || checkedId == R.id.rbLose)
            layoutRate.visibility = if (showRate) android.view.View.VISIBLE else android.view.View.GONE
        }

        //nav to dashboard
        finishbutton.setOnClickListener{
            startActivity(Intent(this, ClientDashboardActivity::class.java))
            finish()
        }

        skipText.setOnClickListener{
            startActivity(Intent(this,ClientDashboardActivity::class.java))
        }
    }




}