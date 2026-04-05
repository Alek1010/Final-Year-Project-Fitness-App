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
import android.app.AlertDialog


//training plan screen
//user can select from pre made workout programs
//view the exercises in the program
//create their own program and log any sets and reps
class TrainingPlanActivity : AppCompatActivity() {

    // Holds whichever program is currently selected in the spinner
    private var selectedProgram: WorkoutProgram? = null

    // Full list shown in spinner = pre-made + custom programs
    private val allPrograms = mutableListOf<WorkoutProgram>()

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

        // These already exist in your XML and now we will actually use them
        val btnChoosePreMade = findViewById<Button>(R.id.btnChoosePreMade)
        val btnCreateCustom = findViewById<Button>(R.id.btnCreateCustom)

        /**
         * Load all pre-made programs first.
         * Later we add custom programs from Firebase into the same spinner list.
         */
        allPrograms.clear()
        allPrograms.addAll(PreMadePrograms.getPrograms())

        /**
         * Load custom programs created by the user from Firebase.
         * Once loaded, combine them with pre-made programs into one spinner.
         */
        WorkoutProgramUtils.loadCustomPrograms(
            onSuccess = { customPrograms ->
                allPrograms.addAll(customPrograms)
                setupProgramSpinner(spinnerPrograms, tvProgramPreview)
            },
            onFailure = {
                // If custom load fails, still show pre-made programs
                setupProgramSpinner(spinnerPrograms, tvProgramPreview)
            }
        )

        /**
         * PRE-MADE BUTTON
         * For now this just reminds the user to use the spinner list.
         */
        btnChoosePreMade.setOnClickListener {
            Toast.makeText(this, "Choose a pre-made program from the list", Toast.LENGTH_SHORT).show()
        }

        /**
         * CREATE CUSTOM BUTTON
         * Opens a dialog where the user can build a simple custom program.
         */
        btnCreateCustom.setOnClickListener {
            showCreateCustomProgramDialog(spinnerPrograms, tvProgramPreview)
        }

        /**
         * SAVE SELECTED PROGRAM BUTTON
         * Saves only the selected program ID into Firebase main user document.
         */
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

        /**
         * SAVE EXERCISE LOG BUTTON
         * Logs one performed set to Firebase.
         */
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

    /**
     * Sets up the spinner using the current list of all programs.
     * This includes:
     * - pre-made programs
     * - user-created custom programs
     */
    private fun setupProgramSpinner(
        spinnerPrograms: Spinner,
        tvProgramPreview: TextView
    ) {
        val programNames = allPrograms.map { it.name }

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
                selectedProgram = allPrograms[position]
                tvProgramPreview.text = buildProgramPreview(selectedProgram!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /**
     * Shows popup dialog for creating a custom program.
     *
     * User enters:
     * - program name
     * - day names
     * - comma separated exercises for each day
     */
    private fun showCreateCustomProgramDialog(
        spinnerPrograms: Spinner,
        tvProgramPreview: TextView
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_custom_program, null)

        val etCustomProgramName = dialogView.findViewById<EditText>(R.id.etCustomProgramName)
        val etDay1Name = dialogView.findViewById<EditText>(R.id.etDay1Name)
        val etDay1Exercises = dialogView.findViewById<EditText>(R.id.etDay1Exercises)
        val etDay2Name = dialogView.findViewById<EditText>(R.id.etDay2Name)
        val etDay2Exercises = dialogView.findViewById<EditText>(R.id.etDay2Exercises)
        val etDay3Name = dialogView.findViewById<EditText>(R.id.etDay3Name)
        val etDay3Exercises = dialogView.findViewById<EditText>(R.id.etDay3Exercises)

        AlertDialog.Builder(this)
            .setTitle("Create Custom Program")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->

                val programName = etCustomProgramName.text.toString().trim()

                if (programName.isEmpty()) {
                    Toast.makeText(this, "Enter a program name", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val days = mutableListOf<WorkoutDay>()

                /**
                 * Build day 1 if user entered data
                 */
                val day1Name = etDay1Name.text.toString().trim()
                val day1ExercisesList = parseExercises(etDay1Exercises.text.toString())
                if (day1Name.isNotEmpty() && day1ExercisesList.isNotEmpty()) {
                    days.add(WorkoutDay(day1Name, day1ExercisesList))
                }

                /**
                 * Build day 2 if user entered data
                 */
                val day2Name = etDay2Name.text.toString().trim()
                val day2ExercisesList = parseExercises(etDay2Exercises.text.toString())
                if (day2Name.isNotEmpty() && day2ExercisesList.isNotEmpty()) {
                    days.add(WorkoutDay(day2Name, day2ExercisesList))
                }

                /**
                 * Build day 3 if user entered data
                 */
                val day3Name = etDay3Name.text.toString().trim()
                val day3ExercisesList = parseExercises(etDay3Exercises.text.toString())
                if (day3Name.isNotEmpty() && day3ExercisesList.isNotEmpty()) {
                    days.add(WorkoutDay(day3Name, day3ExercisesList))
                }

                if (days.isEmpty()) {
                    Toast.makeText(this, "Add at least one day with exercises", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                /**
                 * Build WorkoutProgram object for this user-created plan
                 */
                val customProgram = WorkoutProgram(
                    id = "custom_${programName.lowercase().replace(" ", "_")}",
                    name = programName,
                    days = days
                )

                /**
                 * Save custom program to Firebase
                 */
                WorkoutProgramUtils.saveCustomProgram(
                    program = customProgram,
                    onSuccess = {
                        Toast.makeText(this, "Custom program saved", Toast.LENGTH_SHORT).show()

                        // Add new custom program to current spinner list
                        allPrograms.add(customProgram)
                        setupProgramSpinner(spinnerPrograms, tvProgramPreview)

                        // Select newly created program automatically
                        spinnerPrograms.setSelection(allPrograms.lastIndex)
                    },
                    onFailure = { e ->
                        Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Converts comma-separated exercise input into a clean list.
     *
     * Example input:
     * "Bench Press, Rows, Lateral Raise"
     *
     * Output:
     * listOf("Bench Press", "Rows", "Lateral Raise")
     */
    private fun parseExercises(rawText: String): List<String> {
        return rawText.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    /**
     * Builds readable preview text for selected program.
     */
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
