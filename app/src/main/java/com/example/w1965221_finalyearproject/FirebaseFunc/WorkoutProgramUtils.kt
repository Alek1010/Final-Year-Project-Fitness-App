package com.example.w1965221_finalyearproject.FirebaseFunc

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.w1965221_finalyearproject.client.ExerciseLog
import com.example.w1965221_finalyearproject.client.WorkoutDay
import com.example.w1965221_finalyearproject.client.WorkoutProgram


//handes firebase operations to the workout progams
object WorkoutProgramUtils {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    //save the users selected workout program id
    //user/{uid} slectedprogramId:"upper_lower_4day"
    fun saveSelectedProgram(
        programId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onFailure(Exception("User not logged in"))
            return
        }
        //update only the selectedprogramid field in the user document
        db.collection("user")
            .document(uid)
            .update("selectedProgramId", programId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }


    //loads previously selected workout programid
    // does not load full program data
    //only id stored in firebase
    //then we match it to the local pre made programs
    fun loadSelectedProgram(
        onSuccess: (String?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        db.collection("user")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val programId = document.getString("selectedProgramId")
                onSuccess(programId)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    //save one exercise log one set pewrformed by user
    //user/uid/workoutlogs/workoutDocId
    fun saveExerciseLog(
        date: String,
        workoutName: String,
        programId: String,
        log: ExerciseLog,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        // create workout session id
        val workoutDocId = "${date}_${workoutName.replace(" ", "_")}"
        //basic info about this workout session
        val workoutInfo = hashMapOf(
            "date" to date,
            "workoutName" to workoutName,
            "programId" to programId
        )
        //data for the specific exercise set
        val exerciseInfo = hashMapOf(
            "exerciseName" to log.exerciseName,
            "setNumber" to log.setNumber,
            "reps" to log.reps,
            "weightKg" to log.weightKg
        )

        val workoutDocRef = db.collection("user")
            .document(uid)
            .collection("workoutLogs")
            .document(workoutDocId)

        workoutDocRef.set(workoutInfo)
            .addOnSuccessListener {
                // use auto-generated document id instead of exercise name
                workoutDocRef.collection("exerciseLogs")
                    .add(exerciseInfo)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFailure(e) }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    //save full custome workout program created by user
    //user/uid/customePrograms/programId/days/dayIndex
    fun saveCustomProgram(
        program: WorkoutProgram,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        val programRef = db.collection("user")
            .document(uid)
            .collection("customPrograms")
            .document(program.id)

        val programInfo = hashMapOf(
            "name" to program.name
        )

        // Step 1: save main program document
        programRef.set(programInfo)
            .addOnSuccessListener {

                // Step 2: save each day under /days
                if (program.days.isEmpty()) {
                    onSuccess()
                    return@addOnSuccessListener
                }

                var completedDays = 0
                var failed = false

                for ((index, day) in program.days.withIndex()) {
                    val dayRef = programRef.collection("days").document("day_${index + 1}")

                    val dayInfo = hashMapOf(
                        "name" to day.name
                    )

                    dayRef.set(dayInfo)
                        .addOnSuccessListener {
                            // Step 3: save exercises for this day
                            if (day.exercises.isEmpty()) {
                                completedDays++
                                if (completedDays == program.days.size && !failed) {
                                    onSuccess()
                                }
                                return@addOnSuccessListener
                            }

                            var completedExercises = 0

                            for (exercise in day.exercises) {
                                val exerciseInfo = hashMapOf(
                                    "name" to exercise
                                )

                                dayRef.collection("exercises")
                                    .add(exerciseInfo)
                                    .addOnSuccessListener {
                                        completedExercises++

                                        if (completedExercises == day.exercises.size) {
                                            completedDays++

                                            if (completedDays == program.days.size && !failed) {
                                                onSuccess()
                                            }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        if (!failed) {
                                            failed = true
                                            onFailure(e)
                                        }
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            if (!failed) {
                                failed = true
                                onFailure(e)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    //load all custom programs ceated by user
    //reads main program document, each saved day, eacch days exercices
    fun loadCustomPrograms(
        onSuccess: (List<WorkoutProgram>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        db.collection("user")
            .document(uid)
            .collection("customPrograms")
            .get()
            .addOnSuccessListener { programResult ->

                if (programResult.documents.isEmpty()) {
                    onSuccess(emptyList())
                    return@addOnSuccessListener
                }

                val loadedPrograms = mutableListOf<WorkoutProgram>()
                var completedPrograms = 0
                var failed = false

                for (programDoc in programResult.documents) {
                    val programId = programDoc.id
                    val programName = programDoc.getString("name") ?: "Custom Program"

                    db.collection("user")
                        .document(uid)
                        .collection("customPrograms")
                        .document(programId)
                        .collection("days")
                        .get()
                        .addOnSuccessListener { dayResult ->

                            if (dayResult.documents.isEmpty()) {
                                loadedPrograms.add(
                                    WorkoutProgram(
                                        id = programId,
                                        name = programName,
                                        days = emptyList()
                                    )
                                )

                                completedPrograms++
                                if (completedPrograms == programResult.documents.size && !failed) {
                                    onSuccess(loadedPrograms)
                                }
                                return@addOnSuccessListener
                            }

                            val loadedDays = mutableListOf<WorkoutDay>()
                            var completedDays = 0

                            for (dayDoc in dayResult.documents) {
                                val dayName = dayDoc.getString("name") ?: "Day"

                                dayDoc.reference.collection("exercises")
                                    .get()
                                    .addOnSuccessListener { exerciseResult ->
                                        val exercises = exerciseResult.documents.mapNotNull {
                                            it.getString("name")
                                        }

                                        loadedDays.add(
                                            WorkoutDay(
                                                name = dayName,
                                                exercises = exercises
                                            )
                                        )

                                        completedDays++

                                        if (completedDays == dayResult.documents.size) {
                                            loadedPrograms.add(
                                                WorkoutProgram(
                                                    id = programId,
                                                    name = programName,
                                                    days = loadedDays
                                                )
                                            )

                                            completedPrograms++

                                            if (completedPrograms == programResult.documents.size && !failed) {
                                                onSuccess(loadedPrograms)
                                            }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        if (!failed) {
                                            failed = true
                                            onFailure(e)
                                        }
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            if (!failed) {
                                failed = true
                                onFailure(e)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}