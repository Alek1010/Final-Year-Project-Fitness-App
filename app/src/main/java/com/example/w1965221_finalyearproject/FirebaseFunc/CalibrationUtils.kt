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

        val
    )
}