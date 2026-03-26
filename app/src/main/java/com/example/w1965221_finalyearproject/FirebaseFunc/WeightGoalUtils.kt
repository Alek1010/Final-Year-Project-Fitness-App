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
        onSuccess: (WeightGoalProfile?) -> Unit,
        onFailure:(Exception)-> Unit
    ){
        val currentUser = auth.currentUser
        if(currentUser == null){
            onFailure(Exception("No logged in useer found"))
            return
        }

        val uid = currentUser.uid
        Log.d("WEIGHT_DEBUG", "Loading profile for uid = $uid from collection = $USER_COLLECTION")

        db.collection(USER_COLLECTION).document(uid).get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    Log.d("WEIGHT_DEBUG", "Document does not exist for uid = $uid")
                    onSuccess(null)
                    return@addOnSuccessListener
                }

                Log.d("WEIGHT_DEBUG", "Raw document data = ${document.data}")


                //testing error read as number then convert to double
                val bodyWeight = document.getDouble("bodyWeightKg")
                val weeklyRate = document.getDouble("weeklyRateKg")
                val goalType = document.getString("goalType")

                Log.d("WEIGHT_DEBUG", "bodyWeightRaw = $bodyWeight")
                Log.d("WEIGHT_DEBUG", "weeklyRateRaw = $weeklyRate")
                Log.d("WEIGHT_DEBUG", "goalType = $goalType")



                if (bodyWeight == null || weeklyRate == null || goalType == null){
                    onSuccess(null)
                    return@addOnSuccessListener
                }

                onSuccess(
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