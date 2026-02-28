package com.example.w1965221_finalyearproject.FirebaseFunc

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.w1965221_finalyearproject.auth.LoginActivity
import com.example.w1965221_finalyearproject.client.ClientCalibrationActivity
import com.example.w1965221_finalyearproject.coach.CoachDashboardActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

//holds all firebase create account logic so signup activity stays UI onlyu
//creats firebase auth account email+password
//fire stroe profile docs fullname/email/role/created at
//routs user bacl to login activity
object SignUpUtils {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private const val USER_COLLECTION = "user"

    //create account + create firestore profile
    //activty needs for toast +start acrtivity
    //full name saved into firestore
    //email into firebase auth login identifier
    //passowrd must be min 6 char
    //role stored into firestore for role based routing
    fun creatAccount(
        activity: Activity,
        fullName: String,
        email: String,
        password: String,
        role: String = "client"//defualt if non selcted somehow
    ) {
        //create firebase auth account
        auth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener { result ->
                //unique id for every user
                val uid = result.user?.uid ?:return@addOnSuccessListener

                //build firestroe progile doc
                val userDoc = hashMapOf(
                    "fullName" to fullName,
                    "email" to email,
                    "role" to role,
                    "createdAt" to Timestamp.now()
                )

                //save profile in firestore
                db.collection(USER_COLLECTION).document(uid).set(userDoc)
                    .addOnSuccessListener {
                        Toast.makeText(activity,"Acount created please login", Toast.LENGTH_SHORT).show()

                        if (role == "client") {
                            activity.startActivity(
                                Intent(activity, ClientCalibrationActivity::class.java)
                            )
                        } else {
                            activity.startActivity(
                                Intent(activity, CoachDashboardActivity::class.java)
                            )
                        }
                    }
                    .addOnFailureListener{e ->
                        Log.e("FIRESTROE","Failed to save user progile",e)
                        Toast.makeText(activity,"profile save failed: ${e.message}",Toast.LENGTH_SHORT).show()
                    }

            }
            .addOnFailureListener{e->
                Log.e("AUTH","Signup faled",e)
                Toast.makeText(activity,"Signup failed: ${e.message}",Toast.LENGTH_LONG).show()
            }
    }

}