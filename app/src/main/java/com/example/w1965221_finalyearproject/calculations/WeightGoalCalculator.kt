package com.example.w1965221_finalyearproject.calculations
import kotlin.math.roundToInt

//uses current body weioght
//weekly rate of gain/ lose
//duration in weeks to calc final body weight
//start weight 80kg
//weekly rate -0.5
//duration = 12 weeks
//final weight = 80+(-0.5*12) = 74
object WeightGoalCalculator {
    //holds the full result of the calculation
    data class weightGoalResult(
        val startWeghtKg: Double,
        val weeklyRateKg:Double,
        val durationWeeks: Int,
        val finalGoalWeightKg: Double
    )

    fun calculateGoalWeight(
        startWeghtKg: Double,
        weeklyRateKg: Double,
        durationWeeks: Int
    ): weightGoalResult{
        //raw data before rounding
        val finalWeight = startWeghtKg+(weeklyRateKg* durationWeeks)

        return weightGoalResult(
            startWeghtKg = startWeghtKg,
            weeklyRateKg = weeklyRateKg,
            durationWeeks = durationWeeks,
            //clean up floating points e.g 78.4999*10 = 784.999
            //roundtoint -> 785/10 = 78.5 cleaner final answer
            finalGoalWeightKg = ((finalWeight*10).roundToInt()/10.0)
        )
    }


}