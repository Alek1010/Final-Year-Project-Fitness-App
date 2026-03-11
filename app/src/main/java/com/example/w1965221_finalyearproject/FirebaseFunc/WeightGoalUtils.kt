package com.example.w1965221_finalyearproject.FirebaseFunc
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//loading weight goal info
object WeightGoalUtils {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private const val USER_COLLECTION = "user"

    data class WeightGoalProfile(
        val bodyWeightKg: Double,
        val weeklyRateKg: Double,
        val goalType: String
    )

    fun loadWeightGoalProfile(
        onSucess: (WeightGoalProfile?) -> Unit,
        onFailure:(Exception)-> Unit
    ){
        val currentUser = auth.currentUser
        if(currentUser == null){
            onFailure(Exception("No logged in useer found"))
            return
        }

        val uid = currentUser.uid

        db.collection(USER_COLLECTION).document(uid).get()
            .addOnSuccessListener { document ->
                val bodyWeight = document.getDouble("bodyWeightKg")
                val weeklyRate = document.getDouble("weeklyRateKg")
                val goalType = document.getString("goalType")

                if (bodyWeight == null || weeklyRate == null || goalType == null){
                    onSucess(null)
                    return@addOnSuccessListener
                }

                onSucess(
                    WeightGoalProfile(
                        bodyWeightKg = bodyWeight,
                        weeklyRateKg = weeklyRate,
                        goalType = goalType
                    )
                )
            }
            .addOnFailureListener{ e ->
                Log.e("FIRESTORE", "Failed to load weight goal profile",e)
                onFailure(e)
            }
    }

}