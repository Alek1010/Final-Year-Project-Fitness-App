package com.example.w1965221_finalyearproject.client

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    // Currently selected program from spinner
    private var selectedProgram: WorkoutProgram? = null

    // Full list of available programs = pre-made + custom
    private val allPrograms = mutableListOf<WorkoutProgram>()

    // All previously saved workout sessions
    private val workoutSessions = mutableListOf<WorkoutSession>()
    //if not null coach is editing for that user
    private var viewedClientUid: String? = null

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

        // Only keep custom button now
        val btnCreateCustom = findViewById<Button>(R.id.btnCreateCustom)

        // New workout history UI
        val spinnerWorkoutHistory = findViewById<Spinner>(R.id.spinnerWorkoutHistory)
        val tvWorkoutHistoryPreview = findViewById<TextView>(R.id.tvWorkoutHistoryPreview)

        //read intent extra from the client detailed activity
        viewedClientUid = intent.getStringExtra("client_uid")

        //Load pre-made programs first.
        allPrograms.clear()
        allPrograms.addAll(PreMadePrograms.getPrograms())


        //Then load custom programs and combine them into one spinner.
        WorkoutProgramUtils.loadCustomPrograms(
            userUid = viewedClientUid,
            onSuccess = { customPrograms ->
                allPrograms.addAll(customPrograms)
                setupProgramSpinner(spinnerPrograms, tvProgramPreview)
                applySavedSelectedProgram(spinnerPrograms)
            },
            onFailure = {
                setupProgramSpinner(spinnerPrograms, tvProgramPreview)
                applySavedSelectedProgram(spinnerPrograms)
            }
        )


        //Load previously saved workout sessions into the history dropdown.
        loadWorkoutHistory(spinnerWorkoutHistory, tvWorkoutHistoryPreview)


        //Open dialog for creating a custom program.
        btnCreateCustom.setOnClickListener {
            showCreateCustomProgramDialog(
                spinnerPrograms = spinnerPrograms,
                tvProgramPreview = tvProgramPreview
            )
        }


         //Save selected program ID to Firebase.
        btnSaveProgram.setOnClickListener {
            val program = selectedProgram
            if (program == null) {
                Toast.makeText(this, "Select a program first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            WorkoutProgramUtils.saveSelectedProgram(
                programId = program.id,
                userUid = viewedClientUid,
                onSuccess = {
                    Toast.makeText(this, "Program saved", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }


        //Save one exercise log into Firebase.
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

                    // Refresh workout history after saving
                    loadWorkoutHistory(spinnerWorkoutHistory, tvWorkoutHistoryPreview)
                },
                onFailure = { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }


     //Sets up the program spinner with all available programs.
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


    //Loads all saved workout sessions and fills the history spinner.
    private fun loadWorkoutHistory(
        spinnerWorkoutHistory: Spinner,
        tvWorkoutHistoryPreview: TextView
    ) {
        WorkoutProgramUtils.loadWorkoutSessions(
            onSuccess = { sessions ->
                workoutSessions.clear()
                workoutSessions.addAll(sessions)

                if (workoutSessions.isEmpty()) {
                    tvWorkoutHistoryPreview.text = "No saved workout logs yet"
                    return@loadWorkoutSessions
                }

                val labels = workoutSessions.map {
                    "${storageDateToDisplayDate(it.date)} - ${it.workoutName}"
                }

                val historyAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    labels
                )
                historyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerWorkoutHistory.adapter = historyAdapter

                spinnerWorkoutHistory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: android.view.View?,
                        position: Int,
                        id: Long
                    ) {
                        val session = workoutSessions[position]
                        loadWorkoutSessionDetails(session, tvWorkoutHistoryPreview)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            },
            onFailure = { e ->
                tvWorkoutHistoryPreview.text = "Failed to load workout history: ${e.message}"
            }
        )
    }


    //Loads all exercise logs for one selected workout session
    //and shows them as readable text on screen.
    private fun loadWorkoutSessionDetails(
        session: WorkoutSession,
        tvWorkoutHistoryPreview: TextView
    ) {
        WorkoutProgramUtils.loadExerciseLogsForSession(
            sessionId = session.id,
            onSuccess = { logs ->
                val builder = StringBuilder()

                builder.append("Date: ${storageDateToDisplayDate(session.date)}\n")
                builder.append("Workout: ${session.workoutName}\n\n")

                for (log in logs) {
                    builder.append("${log.exerciseName}\n")
                    builder.append("Set ${log.setNumber} - ${log.reps} reps - ${format1dp(log.weightKg)} kg\n\n")
                }

                tvWorkoutHistoryPreview.text = builder.toString()
            },
            onFailure = { e ->
                tvWorkoutHistoryPreview.text = "Failed to load workout details: ${e.message}"
            }
        )
    }


    //Opens dialog for creating a custom workout program.
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

                val day1Name = etDay1Name.text.toString().trim()
                val day1ExercisesList = parseExercises(etDay1Exercises.text.toString())
                if (day1Name.isNotEmpty() && day1ExercisesList.isNotEmpty()) {
                    days.add(WorkoutDay(day1Name, day1ExercisesList))
                }

                val day2Name = etDay2Name.text.toString().trim()
                val day2ExercisesList = parseExercises(etDay2Exercises.text.toString())
                if (day2Name.isNotEmpty() && day2ExercisesList.isNotEmpty()) {
                    days.add(WorkoutDay(day2Name, day2ExercisesList))
                }

                val day3Name = etDay3Name.text.toString().trim()
                val day3ExercisesList = parseExercises(etDay3Exercises.text.toString())
                if (day3Name.isNotEmpty() && day3ExercisesList.isNotEmpty()) {
                    days.add(WorkoutDay(day3Name, day3ExercisesList))
                }

                if (days.isEmpty()) {
                    Toast.makeText(this, "Add at least one day with exercises", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val customProgram = WorkoutProgram(
                    id = "custom_${programName.lowercase().replace(" ", "_")}",
                    name = programName,
                    days = days
                )

                WorkoutProgramUtils.saveCustomProgram(
                    program = customProgram,
                    userUid = viewedClientUid,
                    onSuccess = {
                        Toast.makeText(this, "Custom program saved", Toast.LENGTH_SHORT).show()

                        allPrograms.add(customProgram)
                        setupProgramSpinner(spinnerPrograms, tvProgramPreview)
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


    //Turns comma-separated text into a clean exercise list.
    private fun parseExercises(rawText: String): List<String> {
        return rawText.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }


    //Builds readable program preview text.
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


    //Converts yyyy-MM-dd into dd/MM/yyyy for nicer display.
    private fun storageDateToDisplayDate(storageDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val parsedDate = inputFormat.parse(storageDate)
            if (parsedDate != null) outputFormat.format(parsedDate) else storageDate
        } catch (e: Exception) {
            storageDate
        }
    }


    //Formats weight to 1 decimal place.
    private fun format1dp(value: Double): String {
        return String.format(Locale.getDefault(), "%.1f", value)
    }

    private fun applySavedSelectedProgram(spinnerPrograms: Spinner) {
        WorkoutProgramUtils.loadSelectedProgram(
            userUid = viewedClientUid,
            onSuccess = { selectedProgramId ->
                if (selectedProgramId == null) return@loadSelectedProgram

                val index = allPrograms.indexOfFirst { it.id == selectedProgramId }
                if (index != -1) {
                    spinnerPrograms.setSelection(index)
                }
            },
            onFailure = {
                // do nothing
            }
        )
    }
}

