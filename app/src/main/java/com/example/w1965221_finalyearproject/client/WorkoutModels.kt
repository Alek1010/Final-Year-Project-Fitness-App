package com.example.w1965221_finalyearproject.client

data class WorkoutProgram(
    val id: String = "",
    val name: String = "",
    val days: List<WorkoutDay> = emptyList()
)

data class WorkoutDay(
    val name: String = "",
    val exercises: List<String> = emptyList()
)

data class ExerciseLog(
    val exerciseName: String = "",
    val setNumber: Int = 1,
    val reps: Int = 0,
    val weightKg: Double = 0.0
)