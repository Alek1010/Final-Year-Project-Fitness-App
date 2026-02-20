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
import com.google.firebase.auth.FirebaseAuth


//login screen
//currently routs directly to client dash board as placeholder
//later need to validate email/password and authenticate via firebase
class LoginActivity : AppCompatActivity() {

    // get FirebaseAuth instance
    private lateinit var auth: FirebaseAuth


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

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //firebse login call async
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    //login success -> go to client dashboard no roles yet
                    startActivity(Intent(this, ClientDashboardActivity::class.java))
                    finish() // stops user going back to login screen
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
}