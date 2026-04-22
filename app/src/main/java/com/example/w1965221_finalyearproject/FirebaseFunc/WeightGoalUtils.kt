package com.example.w1965221_finalyearproject.FirebaseFunc
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//utility object for loading weight gol data from fire base
object WeightGoalUtils {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private const val USER_COLLECTION = "user"
    //container for storeing key values
    data class WeightGoalProfile(
        val bodyWeightKg: Double,
        val weeklyRateKg: Double,
        val goalType: String
    )
    //load weight gaol profile for current loggin user
    //used for when normal client opens their own progress screen
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
        //Debug logging using during dev
        Log.d("WEIGHT_DEBUG", "Loading profile for uid = $uid from collection = $USER_COLLECTION")
        //open users fire store doc
        db.collection(USER_COLLECTION).document(uid).get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    Log.d("WEIGHT_DEBUG", "Document does not exist for uid = $uid")
                    onSuccess(null)
                    return@addOnSuccessListener
                }

                Log.d("WEIGHT_DEBUG", "Raw document data = ${document.data}")


                //Read values saflet
                //fire store nums are read as nums then converted to double
                val bodyWeight = (document.get("bodyWeightKg") as? Number)?.toDouble()
                val weeklyRate = (document.get("weeklyRateKg") as? Number)?.toDouble()
                val goalType = document.getString("goalType")

                Log.d("WEIGHT_DEBUG", "bodyWeightRaw = $bodyWeight")
                Log.d("WEIGHT_DEBUG", "weeklyRateRaw = $weeklyRate")
                Log.d("WEIGHT_DEBUG", "goalType = $goalType")


                //if important value missing return null instead of crashing
                if (bodyWeight == null || weeklyRate == null || goalType == null){
                    onSuccess(null)
                    return@addOnSuccessListener
                }
                //if all exisit return them inside a weightGoalProfile object
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

    //loadf weight goal for specific user UID
    //used for when coach opens selected clients weight progress screen
    //works same however nothing is returned it jist needs to be opened and read
    fun loadWeightGoalProfileForUser(
        userUid: String,
        onSuccess: (WeightGoalProfile?) -> Unit,
        onFailure: (Exception) -> Unit
    ){
        //open selected user firestore doc directly
        db.collection("user")
            .document(userUid)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    onSuccess(null)
                    return@addOnSuccessListener
                }

                val bodyWeight = (document.get("bodyWeightKg") as? Number)?.toDouble()
                val weeklyRate = (document.get("weeklyRateKg") as? Number)?.toDouble()
                val goalType = document.getString("goalType")

                if (bodyWeight != null && weeklyRate != null) {
                    onSuccess(
                        WeightGoalProfile(
                            bodyWeightKg = bodyWeight,
                            weeklyRateKg = weeklyRate,
                            goalType = goalType ?: "maintain"
                        )
                    )
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener{ e ->
                onFailure(e)
            }

    }


}