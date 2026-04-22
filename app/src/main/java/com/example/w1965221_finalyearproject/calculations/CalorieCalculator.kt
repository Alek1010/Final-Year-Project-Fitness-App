package com.example.w1965221_finalyearproject.calculations


import kotlin.math.roundToInt

//calorie calculator
//when auto calries selected macros and calories
//are calculated in this func
//uses Katch mcardle formula requires body fat
//standard activity multipliers
//7700kcal per kg of body weight
object CalorieCalculator {

    //activity multiplier standard values used by many calculators
    enum class ActivityLevel(val multiplier: Double){
        SEDENTARY(1.2),
        LIGHT(1.375),
        MODERATE(1.55),
        VERY_ACTIVE(1.725)
    }

    //Result object holding sll calories 7 values total
    data class CalorieTargets(
        val maintenance: Int,

        val lose025:Int,
        val lose05:Int,
        val lose10:Int,

        val gain025:Int,
        val gain05: Int,
        val gain10:Int
    )

    //main function to be called by the UI to workout the calories
    //takes in body weight
    //body fat percent
    //activity level

    //output object containainig maintanence and t  he 6 other possible options
    fun calculateAllTargets(
        weightKg: Double,
        bodyFatPercent: Double?,
        activityLevel: ActivityLevel
    ):CalorieTargets{
        //lean body nass lbm using nullable
        val leanBodyMassKg = if (bodyFatPercent != null) {
            weightKg * (1.0 - bodyFatPercent / 100.0)
        } else {
            weightKg
        }

        //bmr katch mcardle
        //bmr 370 + 21.6 * LBM
        //estimates calories burnt at rest
        val bmr = 370 + (21.6 * leanBodyMassKg)

        //maintenace calories TDEE
        //TDEE = bmr * activirty multiplier
        val maintenance = (bmr *activityLevel.multiplier).roundToInt()

        //gaal calorien adjustments
        //1kg bodyfat == 7700kcal
        //weekly changes kg -> weekly calories -> daily adjustment
        //daily adjustment = 7700* weekly changekg /7
        //lose = negative
        //gain = positive
        fun goalCalories(weeklyKgChange: Double): Int {
            val dailyAdjustment = (7700.0 * weeklyKgChange) / 7.0
            return (maintenance + dailyAdjustment).roundToInt()
        }

        // LOSS OPTIONS (negative)
        val lose025 = goalCalories(-0.25)
        val lose05 = goalCalories(-0.5)
        val lose10 = goalCalories(-1.0)

        // GAIN OPTIONS (positive)
        val gain025 = goalCalories(0.25)
        val gain05 = goalCalories(0.5)
        val gain10 = goalCalories(1.0)

        // Return all results in one object
        return CalorieTargets(
            maintenance = maintenance,
            lose025 = lose025,
            lose05 = lose05,
            lose10 = lose10,
            gain025 = gain025,
            gain05 = gain05,
            gain10 = gain10
        )

    }
}
