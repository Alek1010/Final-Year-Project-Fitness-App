package com.example.w1965221_finalyearproject.client

data class DailyWeightLog(
    //this class represetns one daily weight log
    //e.g 07/04/2026
    //weightKg = 56.4f
    //weight is logged daily and averge weight for that week is plotted
    //on the graph
    val date: String = "",
    val weightKg: Double = 0.0
)
