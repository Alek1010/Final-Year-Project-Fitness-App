package com.example.w1965221_finalyearproject.FirebaseFunc

import com.example.w1965221_finalyearproject.client.DailyWeightLog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


object WeightLogUtils {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    //save or update one daily weight log into firestore
    //struct user/{uid}/weightlogs/[date]
    //only one entry per day
    //same day can be updated
    //avoids duplicates
    fun saveDailyWeightLog(
        date:String,
        weightKg:Double,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        val uid = auth.currentUser?.uid
        if(uid == null){
            onFailure(Exception("User not logged in"))
            return
        }

        val data = hashMapOf(
            "date" to date,
            "weightKg" to weightKg,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("user")
            .document(uid)
            .collection("weightLogs")
            .document(date)
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener{ e-> onFailure(e)}
    }

    //load all saved weight for current user

    fun loadDailyWeightLogs(
        onSuccess: (List<DailyWeightLog>) -> Unit,
        onFailure: (Exception) -> Unit
    ){
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        db.collection("user")
            .document(uid)
            .collection("weightLogs")
            .get()
            .addOnSuccessListener { result ->
                val logs = result.documents.mapNotNull { document ->
                    val date = document.getString("date")
                    val weightKg = (document.get("weightKg") as? Number)?.toDouble()

                    if (date != null && weightKg != null) {
                        DailyWeightLog(
                            date = date,
                            weightKg = weightKg
                        )
                    } else {
                        null
                    }
                }.sortedBy { it.date }

                onSuccess(logs)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }


     //Save weight progress screen settings into the main user document.
      //These values should persist when leaving and reopening the page.

    fun saveWeightProgressSettings(
        durationWeeks: Int,
        goalWeightKg: Double,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        val updates = hashMapOf<String, Any>(
            "weightProgressDurationWeeks" to durationWeeks,
            "weightProgressGoalWeightKg" to goalWeightKg
        )

        db.collection("user")
            .document(uid)
            .update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    //load saved duration + saved goal weight from the main user document
    fun loadWeightProgressSettings(
    onSuccess: (Int, Double?) -> Unit,
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
                val durationWeeks =
                    (document.get("weightProgressDurationWeeks") as? Number)?.toInt() ?: 12

                val goalWeightKg =
                    (document.get("weightProgressGoalWeightKg") as? Number)?.toDouble()

                onSuccess(durationWeeks, goalWeightKg)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    //optional helper for later if want to delete or edit
    fun deleteDailyWeightLog(
        date: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        db.collection("user")
            .document(uid)
            .collection("weightLogs")
            .document(date)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}