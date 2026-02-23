package com.example.w1965221_finalyearproject.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.client.ClientDashboardActivity
import com.example.w1965221_finalyearproject.coach.CoachDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp


//login screen
//currently routs directly to client dash board as placeholder
//later need to validate email/password and authenticate via firebase
class LoginActivity : AppCompatActivity() {

    // get FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    //firebase database used to fetch users role
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inflates res/layout/activity_login.xml to draw the screen
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()


        val emailInput = findViewById<EditText>(R.id.etEmail)
        val passwordInput = findViewById<EditText>(R.id.etPassword)

        //gives value to the button in the xml so event listen works
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val signUpText = findViewById<TextView>(R.id.tvSignUp)

        ///when user taps login, attempt FireBase sign in
        loginButton.setOnClickListener{
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            //stop empty log ins
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //firebse login call async sign in using email and pass
            //if correct return user object Include UID
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                   result ->
                    val uid = result.user?.uid // used to find user firebase progile doc
                    if(uid == null){
                        // if no uid goes to client testing purposes should not happen
                        //just in case so app doesnt crash
                        routeToClient(clearBackStack = true)
                        return@addOnSuccessListener
                    }

                    //after login look user role in firestore
                    //path is uid
                    db.collection("user").document(uid).get()
                        .addOnSuccessListener { doc ->
                            //if user exist read. role otherwise role is null
                            val role = doc.getString("role")

                            // route based on role default client if missing or unknow
                            when(role){
                                "coach" -> routeToCoach(clearBackStack = true)
                                "client" ->routeToClient(clearBackStack = true)

                                else -> {
                                    //default covers missing roles for testing
                                    routeToClient(clearBackStack = true)

                                    //optional
                                    //create defualt profile so next login consistent
                                    if(!doc.exists()){
                                        createDefaultUserProfile(uid,email)
                                    }
                                }
                            }
                        }
                        .addOnFailureListener{
                            e ->
                            //if firestore read fails network rules still allow access
                            Log.e("FIRESTORE","Failed to load role , defualting to client", e)
                            routeToClient(clearBackStack = true)
                        }

                }
                .addOnFailureListener { e->
                    // login failed show message
                    Log.e("AUTH","Login failed", e)
                    Toast.makeText(this,"login failed: ${e.message}",Toast.LENGTH_LONG).show()
                }

        }


        //navigate to sign up flow
        signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    //create minimal firestore user profile for old accounts that existed in auth beofre starting roles
    private fun createDefaultUserProfile(uid: String, email: String){
        val defaultDoc = hashMapOf(
            "fullName" to "unknown",
            "email" to email,
            "role" to "client", // default role
            "createdAt" to Timestamp.now()
        )

        db.collection("user").document(uid).set(defaultDoc)
            .addOnSuccessListener {
                Log.d("FIRESTORE", "Defualt profile created for $uid")
            }
            .addOnFailureListener{ e ->
                Log.e("FIRESTORE","FAILED to create default profile", e)

            }
    }
    //routing helper keep nav consistent
    //backstack clears all prwevious screenms prevents users from pressing back and going back to login
    //activity after login
    private fun routeToClient(clearBackStack: Boolean){
        //tells android open new screen/ activity
        val intent = Intent(this, ClientDashboardActivity::class.java)
        if(clearBackStack){
            //new task starts new tast
            //clear task removes old screen from stack
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        //launches the activity
        startActivity(intent)
        //finishes so it is fully closed
        finish()
    }

    private fun routeToCoach(clearBackStack: Boolean){
        val intent = Intent(this,CoachDashboardActivity::class.java)
        if (clearBackStack){
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }


}