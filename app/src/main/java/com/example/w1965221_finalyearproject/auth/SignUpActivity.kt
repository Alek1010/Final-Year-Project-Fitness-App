package com.example.w1965221_finalyearproject.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.client.ClientDashboardActivity
import com.example.w1965221_finalyearproject.coach.CoachDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp


//sign up screen
//shows role based routing client nd coach
//later collect user details, create account and persist role to data base
class SignUpActivity : AppCompatActivity() {

    //firebase auth for account creation
    private lateinit var auth: FirebaseAuth

    //Firestore database instnace cloud database
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //draw the xml
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        val emailInput =findViewById<EditText>(R.id.etEmail)
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val createButton = findViewById<Button>(R.id.btnCreateAccount)
        val name = findViewById<EditText>(R.id.etName)
        val roleGroup = findViewById<RadioGroup>(R.id.roleGroup)


        createButton.setOnClickListener{
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val fullName = name.text.toString().trim()

            //basic validation before calling firebase
            if(fullName.isEmpty()|| email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"enter name, email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //fire bases requires password lenght >= 6
            if(password.length <6){
                Toast.makeText(this, "password must be at least 6 character",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //read selected roles
            val role = when(roleGroup.checkedRadioButtonId){
                R.id.radioCoach -> "coach"
                else -> "client"//defualt
            }

            //create firebase auth account with async
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener{
                //only runs if successgul blocks runs
                result ->
                //each user has unique ID UID used inside document ID in firestore
                val uid = result.user?.uid ?: return@addOnSuccessListener

                //save user profile inot firestore using uid as document ID
                //use hasmap key value structure
                val userDoc = hashMapOf(
                    "fullName" to fullName,
                    "email" to email,
                    "role" to role,
                    "createdAt" to Timestamp.now()
                )

                //write profile data into firestore collection users
                db.collection("user").document(uid).set(userDoc).addOnSuccessListener {
                    Toast.makeText(this,"account created. please log in", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,LoginActivity:: class.java))//back to log in
                    finish()
                }.addOnFailureListener{ e -> // if fire store fails
                    Log.e("FIRESTORE","Failed to save user profile", e)
                    Toast.makeText(this,"profile save failure: ${e.message}",Toast.LENGTH_LONG).show()
                }
            }
                .addOnFailureListener { e -> // if authentication failes  e.g email already exist
                    Log.e("AUTH", "signup failed ",e)
                    Toast.makeText(this,"profile save failed: ${e.message}",Toast.LENGTH_LONG).show()
                }



        }




    }

}