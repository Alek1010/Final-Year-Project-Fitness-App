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
        val bodyWeightKg: Double,
        val heightCm: Int,
        val bodyFatPercent: Double,
        val activityLevel: String,

        val targetMode: String,
        val targetCalories: Int,

        val targetProtein: Int? = null,
        val targetCarbs: Int? = null,
        val targetFats: Int? = null,
        val targetWater: Double? = null,

        val goalType:String? = null,
        val weeklyRateKg:Double? = null
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
        //to save to firestore
        //uses key - value pairs similar to json
        val updates = hashMapOf<String,Any>(
            //body stats
            "bodyWeigthKg" to data.bodyWeightKg,
            "heightCm" to data.heightCm,
            "bodyFatPercent" to data.bodyFatPercent,
            //activity level
            "activityLevel" to data.activityLevel,
            //mode of calories entered
            "targetMode" to data.targetMode,
            //final calorie count
            "targetCalories" to data.targetCalories
        )
        // might not exist based on mode
        //manual user inserts macros and it is give
        //auto calculates calories and then insers macros based calories
        //so if a field is null no error should error
        data.targetProtein?.let { updates["targetProtein"] = it }//if exsit add
        data.targetCarbs?.let { updates["targetCarbs"] = it }
        data.targetFats?.let { updates["targetFats"] = it }
        data.targetWater?.let { updates["targetWater"] = it }
        data.goalType?.let { updates["goalType"] = it }
        data.weeklyRateKg?.let { updates["weeklyRateKg"] = it }

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