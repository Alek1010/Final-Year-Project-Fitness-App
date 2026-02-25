package com.example.w1965221_finalyearproject.FirebaseFunc

import android.util.Log
import android.widget.TextView
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
}