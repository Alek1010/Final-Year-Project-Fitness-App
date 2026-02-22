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


//sign up screen
//shows role based routing client nd coach
//later collect user details, create account and persist role to data base
class SignUpActivity : AppCompatActivity() {

    //firebase auth for account creation
    private lateinit var auth: FirebaseAuth

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

            //basic validation before calling firebase
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //fire bases requires password lenght >= 6
            if(password.length <6){
                Toast.makeText(this, "password must be at least 6 character",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //create user in firebase authentication
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                Toast.makeText(this,"account created please login",Toast.LENGTH_SHORT).show()
                //after signup return to login screen
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
                .addOnFailureListener{e ->
                    Log.e("AUTH","signup failed", e)
                    Toast.makeText(this,"signup failed: ${e.message}",Toast.LENGTH_LONG).show()
                }

        }




    }

}