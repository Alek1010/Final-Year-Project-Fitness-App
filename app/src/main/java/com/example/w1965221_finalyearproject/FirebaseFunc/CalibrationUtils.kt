package com.example.w1965221_finalyearproject.FirebaseFunc
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.w1965221_finalyearproject.client.ClientDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


//Calibrations handles firestore saving client
//calibration screen saves the info user gives to there doc
//seperates logic
object CalibrationUtils {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private const val USER_COLLECTION = "user"

    //data holder for everything to be saved
    //from the client calibration screen
    data class ClientCalibrationData(
        val bodyWeightKG: Double,
        val heightCm: Int,
        val bodyFatPercent: Double,
        val activityLevel: String,

        val targetMode: String,
        val targetCalories: Int,

        val targetProtein: Int? = null,
        val targetCarbs: Int? = null,
        val targetFats: Int? = null,
        val targetWater: Double? = null
    )

    //save calibration data into current client firebase document
    fun saveClientCalibration(
        activity:Activity,
        data: ClientCalibrationData
    ){
        val currentUser = auth.currentUser

        if(currentUser == null){
            Toast.makeText(activity,"no loggedin user found",Toast.LENGTH_SHORT).show()
            return
        }

        val uid = currentUser.uid

        //build Firestore updata map
        val updates = hashMapOf<String,Any>(
            "bodyWeigthKg" to data.bodyWeightKG,
            "heightCm" to data.heightCm,
            "bodyFatPercent" to data.bodyFatPercent,
            "activityLevel" to data.activityLevel,
            "targetMode" to data.targetMode,
            "targetCalories" to data.targetCalories
        )

        data.targetProtein?.let { updates["targetProtein"] = it }
        data.targetCarbs?.let { updates["targetCarbs"] = it }
        data.targetFats?.let { updates["targetFats"] = it }
        data.targetWater?.let { updates["targetWater"] = it }

        db.collection(USER_COLLECTION)
            .document(uid)
            .set(updates, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(activity,"progile set up saved",Toast.LENGTH_SHORT).show()
                val intent = Intent(activity,ClientDashboardActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
            .addOnFailureListener{ e ->
                Log.e("FIRESTORE", "failed to save calibration", e)
                Toast.makeText(activity,"saved failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}