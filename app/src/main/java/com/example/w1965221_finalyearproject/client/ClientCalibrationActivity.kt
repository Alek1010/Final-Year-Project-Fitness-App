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
import android.widget.Toast
import com.example.w1965221_finalyearproject.calculations.CalorieCalculator
import com.example.w1965221_finalyearproject.calculations.MacroCalculator
import com.example.w1965221_finalyearproject.FirebaseFunc.CalibrationUtils


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

        val weightInput = findViewById<EditText>(R.id.etBodyWeight)
        val heightInput = findViewById<EditText>(R.id.etHeight)
        val bodyFatInput = findViewById<EditText>(R.id.etBodyFat)
        val rgActivityLevel = findViewById<RadioGroup>(R.id.rgActivityLevel)

        val autoPreviewText = findViewById<TextView>(R.id.tvAutoPreview)

        val rbManual = findViewById<android.widget.RadioButton>(R.id.rbManual)
        val rbAuto = findViewById<android.widget.RadioButton>(R.id.rbAuto)

        val rbMaintain = findViewById<android.widget.RadioButton>(R.id.rbMaintain)
        val rbGain = findViewById<android.widget.RadioButton>(R.id.rbGain)
        val rbLose = findViewById<android.widget.RadioButton>(R.id.rbLose)

        val rgRate = findViewById<RadioGroup>(R.id.rgRate)


        //helper safley read frams from edittext if input ==
        //50g it wont crash
        fun getInt(editText: EditText):Int{
            return editText.text.toString().toIntOrNull() ?: 0
        }

        //safly read decimal valuesnfrom editText if input ==82.2kg
        fun getDouble(editText: EditText):Double{
            return editText.text.toString().toDoubleOrNull()?:0.0
        }

        // safely read optional decimal values
        // if blank -> return null instead of crashing
        fun getNullableDouble(editText: EditText): Double? {
            val text = editText.text.toString().trim()
            return if (text.isEmpty()) null else text.toDoubleOrNull()
        }

        //recalculate caloreis and update ui
        fun updateCalories(){
            val protein = getInt(proteinInput)
            val carbs = getInt(carbsInput)
            val fats = getInt(fatsInput)

            val calories = (protein*4)+(carbs*4)+(fats*9)
            caloriesText.text = "$calories kcal"
        }

        //convert selected radio button into calculator enum
        fun getSelectedActivityLevel(): CalorieCalculator.ActivityLevel?{
            return when (rgActivityLevel.checkedRadioButtonId){
                R.id.rbSedentary -> CalorieCalculator.ActivityLevel.SEDENTARY
                R.id.rbLightlyActive -> CalorieCalculator.ActivityLevel.LIGHT
                R.id.rbModerate -> CalorieCalculator.ActivityLevel.MODERATE
                R.id.rbVeryActive -> CalorieCalculator.ActivityLevel.VERY_ACTIVE
                else -> null
            }
        }


        //helper returns actvivty level as string
        fun getSelectedActivityLevelText():String?{
            return when (rgActivityLevel.checkedRadioButtonId){
                R.id.rbSedentary -> "sedentary"
                R.id.rbLightlyActive ->"light"
                R.id.rbModerate->"moderate"
                R.id.rbVeryActive->"very_active"
                else->null
            }
        }

        //helper convers selected goal as text
        fun getSelectedGoalType(): String?{
            return when {
                rbMaintain.isChecked ->"maintain"
                rbLose.isChecked ->"lose"
                rbGain.isChecked -> "gain"
                else -> null
            }
        }

        //helper convers selected weekly rate a a number
        //negative = lose, positive = gain 0 = maintain
        fun getSelectedWeeklyRateKg(): Double? {
            return when {
                rbMaintain.isChecked -> 0.0

                rbLose.isChecked && rgRate.checkedRadioButtonId == R.id.rbMild -> -0.25
                rbLose.isChecked && rgRate.checkedRadioButtonId == R.id.rbStandard -> -0.5
                rbLose.isChecked && rgRate.checkedRadioButtonId == R.id.rbExtreme -> -1.0

                rbGain.isChecked && rgRate.checkedRadioButtonId == R.id.rbMild -> 0.25
                rbGain.isChecked && rgRate.checkedRadioButtonId == R.id.rbStandard -> 0.5
                rbGain.isChecked && rgRate.checkedRadioButtonId == R.id.rbExtreme -> 1.0

                else -> null
            }
        }

        //RUNs the auto calorie calculator snd return calorie value
        fun calculateSelectedAutoCalories(): Int? {
            val weight = getDouble(weightInput)
            val bodyFat = getNullableDouble(bodyFatInput)
            val activityLevel = getSelectedActivityLevel() ?: return null

            //calculate all 7 targets
            val targets = CalorieCalculator.calculateAllTargets(
                weightKg = weight,
                bodyFatPercent = bodyFat,
                activityLevel = activityLevel
            )

            //maintain does not need a rate
            if(rbMaintain.isChecked){
                return targets.maintenance
            }

            //gain / lose need more options
            val selectedRateId = rgRate.checkedRadioButtonId


            return when{
                rbLose.isChecked && selectedRateId == R.id.rbMild -> targets.lose025
                rbLose.isChecked && selectedRateId == R.id.rbStandard -> targets.lose05
                rbLose.isChecked && selectedRateId == R.id.rbExtreme -> targets.lose10

                rbGain.isChecked && selectedRateId == R.id.rbMild -> targets.gain025
                rbGain.isChecked && selectedRateId == R.id.rbStandard -> targets.gain05
                rbGain.isChecked && selectedRateId == R.id.rbExtreme -> targets.gain10

                else -> null
            }
        }


        //automatic preview text if auto selected
        fun updateAutoPreview(){
            if(!rbAuto.isChecked) return

            val autoCalories = calculateSelectedAutoCalories()

            if(autoCalories != null){
                autoPreviewText.text = "calculated target: $autoCalories kcal"
            } else{
                autoPreviewText.text = "Selected goal rate to calculate target "
            }
        }




        //one shared watcher fro all three feilds
        val watcher = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateCalories()
                updateAutoPreview()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        //attch watcher to all marco inputs
        proteinInput.addTextChangedListener(watcher)
        carbsInput.addTextChangedListener(watcher)
        fatsInput.addTextChangedListener(watcher)

        //recalculare auto preview when body stats change
        weightInput.addTextChangedListener(watcher)
        bodyFatInput.addTextChangedListener(watcher)

        //update auto preview when target mode changes

        //set initaial value to 0kcal
        updateCalories()

        //switch between manaula and auto selections
        rgTargetMode.setOnCheckedChangeListener{ _,checkedId ->
            val isManual = checkedId == R.id.rbManual
            layoutManual.visibility = if (isManual) android.view.View.VISIBLE else android.view.View.GONE
            layoutAuto.visibility = if (isManual) android.view.View.GONE else android.view.View.VISIBLE

            updateAutoPreview()
        }

        //show rate option only when gain or lose weight is selected
        rgGaol.setOnCheckedChangeListener { _, checkedId ->
            val showRate = (checkedId == R.id.rbGain || checkedId == R.id.rbLose)
            layoutRate.visibility = if (showRate) android.view.View.VISIBLE else android.view.View.GONE

            updateAutoPreview()
        }

        //update auto preview when rate changes
        rgRate.setOnCheckedChangeListener{_,_->
            updateAutoPreview()
        }

        //update auto preview when activity level changfes
        rgActivityLevel.setOnCheckedChangeListener{_,_->
            updateAutoPreview()
        }

        //nav to dashboard
        finishbutton.setOnClickListener{

            val weight = weightInput.text.toString().trim()
            val height = heightInput.text.toString().trim()
            val bodyFat = getNullableDouble(bodyFatInput)
            val selectedActivtyId = rgActivityLevel.checkedRadioButtonId
            val activityLevelText = getSelectedActivityLevelText() ?: "unknown"
            val goalType = getSelectedGoalType()
            val weeklyRateKg = getSelectedWeeklyRateKg()

            //mandatory validation
            if(weight.isEmpty()){
                weightInput.error = "enter body weight"
                return@setOnClickListener
            }

            if (height.isEmpty()){
                heightInput.error = "enter height"
                return@setOnClickListener
            }

            //if (bodyFat.isEmpty()){
            //    heightInput.error = "enter body fat %"
            //    return@setOnClickListener
            //}

            if(selectedActivtyId ==-1){
                Toast.makeText(this,"Selecte activty level",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (goalType == null) {
                Toast.makeText(this, "Select a goal", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if ((goalType == "lose" || goalType == "gain") && weeklyRateKg == null) {
                Toast.makeText(this, "Select a rate", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //manual mode validation
            if(rgTargetMode.checkedRadioButtonId == R.id.rbManual){
                val protein = getInt(proteinInput)
                val carbs = getInt(carbsInput)
                val fats = getInt(fatsInput)

                val calories = (protein * 4) + (carbs * 4)+ (fats*9)

                val calibrationData = CalibrationUtils.ClientCalibrationData(
                    bodyWeightKg = weight.toDouble(),
                    heightCm = height.toInt(),
                    bodyFatPercent = bodyFat,
                    activityLevel =activityLevelText ,

                    targetMode = "manual",
                    targetCalories =  calories,
                    targetProtein = protein,
                    targetCarbs = carbs,
                    targetFats = fats,
                    goalType = goalType,
                    weeklyRateKg = weeklyRateKg
                )

                CalibrationUtils.saveClientCalibration(this,calibrationData)
                return@setOnClickListener

            }

            //validation for auto mode
            if(rbAuto.isChecked){
                //calc the calories
                val autoCalories = calculateSelectedAutoCalories()
                if (autoCalories == null) {
                    Toast.makeText(this, "Unable to calculate calories", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener

                }
                //calc the macros
                val autoMacros = MacroCalculator.calculateMacros(
                    weightKg = weight.toDouble(),
                    calories = autoCalories
                )


                // For now just show result in preview before moving on
                autoPreviewText.text = "Calculated target: $autoCalories kcal"

                val calibrationData = CalibrationUtils.ClientCalibrationData(
                    bodyWeightKg = weight.toDouble(),
                    heightCm = height.toInt(),
                    bodyFatPercent = bodyFat,
                    activityLevel = activityLevelText,
                    targetMode = "auto",
                    targetCalories = autoCalories,
                    targetProtein = autoMacros.proteinGrams,
                    targetCarbs = autoMacros.carbsGrams,
                    targetFats = autoMacros.fatGrams,
                    goalType = goalType,
                    weeklyRateKg = weeklyRateKg
                )

                CalibrationUtils.saveClientCalibration(this,calibrationData)
                return@setOnClickListener
            }

            //if all the validations pass -> continue
            startActivity(Intent(this, ClientDashboardActivity::class.java))
            finish()
        }

        skipText.setOnClickListener{
            startActivity(Intent(this,ClientDashboardActivity::class.java))
        }
    }




}