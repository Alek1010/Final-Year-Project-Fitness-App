package com.example.w1965221_finalyearproject.client
//full workout program
data class WorkoutProgram(
    val id: String = "",
    val name: String = "",
    val days: List<WorkoutDay> = emptyList()
)
//one day in the program
data class WorkoutDay(
    val name: String = "",
    val exercises: List<String> = emptyList()
)
//logs the set by the user
data class ExerciseLog(
    val exerciseName: String = "",
    val setNumber: Int = 1,
    val reps: Int = 0,
    val weightKg: Double = 0.0
)

//shows one saved workout session
data class WorkoutSession(
    val id: String = "",
    val date: String = "",
    val workoutName: String = "",
    val programId: String = "",
    val exerciseLogs: List<ExerciseLog> = emptyList()
)