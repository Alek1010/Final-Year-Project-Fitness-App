package com.example.w1965221_finalyearproject.FirebaseFunc

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.w1965221_finalyearproject.client.ExerciseLog


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
}