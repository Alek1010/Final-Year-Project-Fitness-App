package com.example.w1965221_finalyearproject.calculations

import kotlin.math.roundToInt

//macro calculator  when auto selected
//rules
//protein 1.8g per kg bodyweight
//22% of total calories
//carbs = remaining calories
object MacroCalculator {

    data class MacroTargets(
        val proteinGrams: Int,
        val carbsGrams: Int,
        val fatGrams: Int
    )

    fun calculateMacros(
        weightKg:Double,
        calories: Int
    ):MacroTargets{
        //protein calculation
        //1.8 per kg
        val proteinGrams = (weightKg*1.8).roundToInt()

        //convert protein grams to calories
        val proteinCalories = proteinGrams

        //fat calc
        val fatCalories = (calories*0.22)

        //convert calories to grams
        val fatGrams = (fatCalories/9).roundToInt()

        val fatsCalories = fatGrams*9

        //carb calculation
        val remainingCalories = calories - proteinCalories- fatsCalories
        val carbsGrams = (remainingCalories/4)

        return MacroTargets(
            proteinGrams = proteinGrams,
            carbsGrams = carbsGrams,
            fatGrams = fatGrams
        )


    }

}