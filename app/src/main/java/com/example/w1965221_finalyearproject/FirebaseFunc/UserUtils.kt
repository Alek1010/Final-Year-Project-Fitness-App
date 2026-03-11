package com.example.w1965221_finalyearproject.FirebaseFunc

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.TextView
import com.example.w1965221_finalyearproject.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//reusable helper object for user related fire store operations
//multiple activities can reuse same logic

object UserUtils{
    //loads the loggin in users full name from firestore and updates the welcome text

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val USER_COLLECTION = "user"

    fun loadUserName(welcomeText: TextView){


        //get the current user
        val currentUser = FirebaseAuth.getInstance().currentUser

        //safty check user should always exist
        if(currentUser == null){
            welcomeText.text = "welcome back!"
            return
        }

        val uid = currentUser.uid

        //fetch users firestore profile documents
        db.collection(USER_COLLECTION).document(uid).get()
            .addOnSuccessListener { document ->
                val fullName = document.getString("fullName")?: ""

                //update ui to show name
                welcomeText.text = "Welcome back, $fullName"
            }
            .addOnFailureListener{ e ->
                Log.e("FIRESTORE","Failed to load the user name", e)
                welcomeText.text = "Welcome back"
            }

    }

    //log out func logs out current user send them back to login page
    //remove repeated code
    fun logout(currentActivity: Activity){
        //sign out from firebase authentication
        FirebaseAuth.getInstance().signOut()

        //create intent to go back to login screen
        val intent = Intent(currentActivity, LoginActivity::class.java)

        //clear back stack so user cannot press back to dashboard
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        currentActivity.startActivity(intent)
        //finish current activity
        currentActivity.finish()
    }


    //load client calories and macros to the overview on dashboard
    fun loadClientOverview(overviewTextView: TextView){
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null){
            overviewTextView.text = "Calories: -\nProtein: -\nCarbs: -\nFats: "
            return
        }

        val uid = currentUser.uid
    // readinf from the firestroe
        db.collection(USER_COLLECTION).document(uid).get()
            //when loaded sucessfuly
            .addOnSuccessListener { document ->
                //even if value is saved as int firestore stores numbers internally as
                //long
                val calories = document.getLong("targetCalories")?.toInt()
                val protein = document.getLong("targetProtein")?.toInt()
                val carbs = document.getLong("targetCarbs")?.toInt()
                val fats = document.getLong("targetFats")?.toInt()

                val caloriesText = calories?.let { "$it kcal" }?:"-"// formating ui if null -> "-"
                val proteinText = protein?.let { "${it}g" }?:"-"
                val carbsText = carbs?.let { "${it}g" }?:"-"
                val fatsText = fats?.let { "${it}g" }?:"-"

                overviewTextView.text =
                    "Calories: $caloriesText\n"+ "Protein: $proteinText\n"+"Carbs: $carbsText\n"+
                            "Fats: $fatsText"

            }
            .addOnFailureListener{
                overviewTextView.text = "Calories: -\nProtein: -\nCarbs: -\nFats: "
            }
    }

}