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
        val bodyFatPercent: Double? = null,
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
            "bodyWeightKg" to data.bodyWeightKg,
            "heightCm" to data.heightCm,
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
        // only save body fat if user actually entered it
        data.bodyFatPercent?.let { updates["bodyFatPercent"] = it }
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

    //holds existing calibration values loaded back from firebase
    //so they can be shown again in the calibration stage
    data class ExistingCalibrationData(
        val bodyWeightKg: Double? = null,
        val heightCm: Int? = null,
        val bodyFatPercent: Double? = null,
        val activityLevel: String? = null,
        val targetMode: String? = null,
        val targetProtein: Int? = null,
        val targetCarbs: Int? = null,
        val targetFats: Int? = null,
        val goalType: String? = null,
        val weeklyRateKg: Double? = null
    )

    //loads current users saved calibration valies from
    //firebase when reopening calibration screen
    //from profile page
    fun loadClientCalibration(
        onSuccess: (ExistingCalibrationData?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onFailure(Exception("No logged in user found"))
            return
        }

        val uid = currentUser.uid

        db.collection(USER_COLLECTION)
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    onSuccess(null)
                    return@addOnSuccessListener
                }

                val data = ExistingCalibrationData(
                    bodyWeightKg = (document.get("bodyWeightKg") as? Number)?.toDouble(),
                    heightCm = (document.get("heightCm") as? Number)?.toInt(),
                    bodyFatPercent = (document.get("bodyFatPercent") as? Number)?.toDouble(),
                    activityLevel = document.getString("activityLevel"),
                    targetMode = document.getString("targetMode"),
                    targetProtein = (document.get("targetProtein") as? Number)?.toInt(),
                    targetCarbs = (document.get("targetCarbs") as? Number)?.toInt(),
                    targetFats = (document.get("targetFats") as? Number)?.toInt(),
                    goalType = document.getString("goalType"),
                    weeklyRateKg = (document.get("weeklyRateKg") as? Number)?.toDouble()
                )

                onSuccess(data)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

}