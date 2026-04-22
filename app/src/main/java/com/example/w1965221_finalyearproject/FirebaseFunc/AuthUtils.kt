package com.example.w1965221_finalyearproject.FirebaseFunc

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.w1965221_finalyearproject.client.ClientDashboardActivity
import com.example.w1965221_finalyearproject.coach.CoachDashboardActivity
import com.google.firebase.firestore.SetOptions


//centralised auth + role routing logic used bt login activity
object AuthUtils {
    //firebase authentiation instance
    //lazy will only be created when first used
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    //collection name in the console
    private const val USER_COLLECTION = "user"

    //signs user in with fire base auth
    //loads their firestore role or creating default profile if missing
    //finally routes them to correct dash board
    //main logic entry
    //1 authenticates user with email and password
    //2 if successfull -> loafs role from firestore
    //3 if role missing -> creates default profile
    //4 routes user to correct dashboard
    fun loginAndRoute(
        activity: Activity,
        email: String,
        password: String
    ){
        //authentication
        auth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener { result ->
                //uid is unique id for account
                val uid = result.user?.uid ?: return@addOnSuccessListener
                loadRoleOrCreateDefault(activity,uid,email) // check fire store for role
            }
            .addOnFailureListener{ e ->
                Log.e("AUTH","Login failed",e)
                Toast.makeText(activity,"login failed: ${e.message}",Toast.LENGTH_LONG).show()
            }// if fialed show messafe
    }

    //Read the role from firebase
    //if user doc does not exist older account create defualt
    //testing purposes during inital development
    //default role. = client
    private fun loadRoleOrCreateDefault(
        activity: Activity,
        uid: String,
        email: String
    ){
        //ref to firestore document
        val userRef = db.collection(USER_COLLECTION).document(uid)

        //asyncornous fire store read
        userRef.get()
            //if success
            .addOnSuccessListener { doc ->
                //if doc doesnt exist happens if account created before fire store was added
                if(!doc.exists()){
                    //no profile doc = create default one
                    createDefaultUserProfile(
                        activity = activity,
                        uid = uid,
                        email = email,
                        role = "client"
                    )
                    return@addOnSuccessListener
                }
                //if exisit read the role two options client or coach
                val role = doc.getString("role") ?: "client"
                //route to the screen
                routeByRole(activity,role)
            }
            //if fire store fails what ever reason
            .addOnFailureListener{e ->
                Log.e("FIRESTORE","Failed to read user profile", e)
                Toast.makeText(activity,"Profile load failed: ${e.message}", Toast.LENGTH_LONG).show()
                //safe fallback -> routes as client
                routeByRole(activity,"client")
            }
    }

    //create default fire store user profile doc
    //for testing old accounts during inital dev that in auth but not in fire store
    //Setoption. merge() if feild exist not overwirten
    //if missing added
    private fun createDefaultUserProfile(
        activity: Activity,
        uid: String,
        email: String,
        role: String
    ){
        val userRef = db.collection(USER_COLLECTION).document(uid)

        val defaultDoc = hashMapOf(
            "email" to email,
            "fullName" to "User", // fallback name can be edited
            "role" to role
        )

        //merge = dont overwrite if fields alreadt exist
        userRef.set(defaultDoc,SetOptions.merge())
            .addOnSuccessListener {
                //after creating route normally
                routeByRole(activity,role)
            }
            .addOnFailureListener{ e ->
                Log.e("FIRESTORE","Failed to create a default profile",e)
                Toast.makeText(activity,"default profile failed: ${e.message}",Toast.LENGTH_LONG).show()
                //fallback route
                routeByRole(activity,"client")
            }
    }

    //route user to correct dashboard based on role
    //default is client
    private fun routeByRole(activity: Activity,role:String){
        val intent = when(role.lowercase()) {
            "coach" -> Intent(activity, CoachDashboardActivity::class.java)
            else -> Intent(activity,ClientDashboardActivity::class.java)
            }

            //clear back stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()


    }

}