package com.example.w1965221_finalyearproject.calculations

import com.example.w1965221_finalyearproject.client.ClientCalibrationActivity
import kotlin.math.roundToInt

//calorie calculator
//when auto calries selected macros and calories
//are calculated in this func
//uses Katch mcardle formula requires body fat
//standard activity multipliers
//7700kcal per kg of body weight
object CalorieCalculator {
    //lean body mass
    private fun calculateLBM(weightKG:Double,bodyFatPercent:Double):Double{
        return weightKG * (1-bodyFatPercent/100)
    }

    //bmr fomular
    //bmr = 370 + (21.6*lbm)
    private fun calculateBMR(weightKG: Double, bodyFatPercent: Double):Double{
        val lbm = calculateLBM(weightKG,bodyFatPercent)
        return 370 + (21.6*lbm)
    }

    //standar activty modifiers
    private fun getActivityMultiplier(activityLevel: ActivityLevel):Double{
        return when (activityLevel){
            ActivityLevel.SEDENTARY -> 1.2
            ActivityLevel.LIGHT -> 1.375
            ActivityLevel.MODERATE -> 1.55
            ActivityLevel.VERY_ACTIVE -> 1.725

        }
    }

    //maintenance Calories TDEE
    //TDEE = BMR *Activity muliplier
    fun calculateMaintenanceCalories(
        weightKG: Double,
        bodyFatPercent: Double,
        activityLevel: ActivityLevel
    ): Int{
        val bmr = calculateBMR(weightKG, bodyFatPercent)
        val multiplier = getActivityMultiplier(activityLevel)
        val tdee = bmr * multiplier
        return tdee.roundToInt()
    }


    //goal calories
    //1kg bodyfat == 7700kcal
    //daily calorie change = 770*weeklyGoalKG /7
    //week;y goal kg
    //-0.5kg
    fun calculateGoalCalories(
        maintenanceCalories: Int,
        weeklyGoalKg: Double
    ):Int{
        val dailyAdjustment = (7700*weeklyGoalKg)/7
        return (maintenanceCalories+dailyAdjustment).roundToInt()
    }

}