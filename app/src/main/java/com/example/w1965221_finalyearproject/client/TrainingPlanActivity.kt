package com.example.w1965221_finalyearproject.client

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.w1965221_finalyearproject.R

class TrainingPlanActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_plan)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerExercises)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val fakeExercises = listOf(
            Exercise("Barbell Squat", "Quads", "3 × 5 @ 120kg"),
            Exercise("Leg Press", "Quads", "3 × 10"),
            Exercise("Hamstring Curl", "Hamstrings", "3 × 12")
        )

        recyclerView.adapter = ExerciseAdapter(fakeExercises)
    }

}