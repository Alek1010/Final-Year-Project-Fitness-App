package com.example.w1965221_finalyearproject.client

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.w1965221_finalyearproject.R

class ExerciseAdapter(private val exercises: List<Exercise>)
    : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>(){

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvExerciseName)
        val muscle: TextView = itemView.findViewById(R.id.tvMuscleGroup)
        val sets: TextView = itemView.findViewById(R.id.tvSets)
        val logButton: Button = itemView.findViewById(R.id.btnLogSet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun getItemCount(): Int = exercises.size

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.name.text = exercise.name
        holder.muscle.text = exercise.muscleGroup
        holder.sets.text = exercise.sets

        holder.logButton.setOnClickListener {
            showLogDialog(holder.itemView.context, exercise.name)
        }
    }

    private fun showLogDialog(context: android.content.Context, exerciseName: String) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_log_set, null)

        val weightInput = dialogView.findViewById<EditText>(R.id.etWeight)
        val repsInput = dialogView.findViewById<EditText>(R.id.etReps)

        AlertDialog.Builder(context)
            .setTitle("Log $exerciseName")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val weight = weightInput.text.toString()
                val reps = repsInput.text.toString()

                Toast.makeText(
                    context,
                    "Logged: $weight kg × $reps reps",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}