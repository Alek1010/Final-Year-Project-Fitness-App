package com.example.w1965221_finalyearproject.client

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.w1965221_finalyearproject.R
import android.widget.*

import com.example.w1965221_finalyearproject.FirebaseFunc.WorkoutProgramUtils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//training plan screen
//uses recyclerView bescause training sessions are naturally a list of excersies
//placeholder data is sued in this will later come from a data base
class TrainingPlanActivity : AppCompatActivity(){

    private var selectedProgram: WorkoutProgram? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_plan)

        val spinnerPrograms = findViewById<Spinner>(R.id.spinnerPrograms)
        val btnSaveProgram = findViewById<Button>(R.id.btnSaveProgram)
        val tvProgramPreview = findViewById<TextView>(R.id.tvProgramPreview)

        val etExerciseName = findViewById<EditText>(R.id.etExerciseName)
        val etSetNumber = findViewById<EditText>(R.id.etSetNumber)
        val etReps = findViewById<EditText>(R.id.etReps)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val btnSaveLog = findViewById<Button>(R.id.btnSaveLog)

        val programs = PreMadePrograms.getPrograms()
        val programNames = programs.map { it.name }

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            programNames
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPrograms.adapter = spinnerAdapter

        spinnerPrograms.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                selectedProgram = programs[position]
                tvProgramPreview.text = buildProgramPreview(selectedProgram!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSaveProgram.setOnClickListener {
            val program = selectedProgram
            if (program == null) {
                Toast.makeText(this, "Select a program first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            WorkoutProgramUtils.saveSelectedProgram(
                programId = program.id,
                onSuccess = {
                    Toast.makeText(this, "Program saved", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }

        btnSaveLog.setOnClickListener {
            val program = selectedProgram
            if (program == null) {
                Toast.makeText(this, "Select a program first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val exerciseName = etExerciseName.text.toString().trim()
            val setNumber = etSetNumber.text.toString().toIntOrNull()
            val reps = etReps.text.toString().toIntOrNull()
            val weight = etWeight.text.toString().toDoubleOrNull()

            if (exerciseName.isEmpty() || setNumber == null || reps == null || weight == null) {
                Toast.makeText(this, "Fill all log fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val log = ExerciseLog(
                exerciseName = exerciseName,
                setNumber = setNumber,
                reps = reps,
                weightKg = weight
            )

            WorkoutProgramUtils.saveExerciseLog(
                date = today,
                workoutName = "Manual Workout Entry",
                programId = program.id,
                log = log,
                onSuccess = {
                    Toast.makeText(this, "Exercise log saved", Toast.LENGTH_SHORT).show()
                    etExerciseName.text.clear()
                    etSetNumber.text.clear()
                    etReps.text.clear()
                    etWeight.text.clear()
                },
                onFailure = { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun buildProgramPreview(program: WorkoutProgram): String {
        val builder = StringBuilder()
        builder.append(program.name).append("\n\n")

        for (day in program.days) {
            builder.append(day.name).append("\n")
            for (exercise in day.exercises) {
                builder.append("- ").append(exercise).append("\n")
            }
            builder.append("\n")
        }

        return builder.toString()

}

}