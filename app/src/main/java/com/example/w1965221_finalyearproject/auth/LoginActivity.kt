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
import com.example.w1965221_finalyearproject.FirebaseFunc.AuthUtils


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

        //when user presses login button fire base sign in
        loginButton.setOnClickListener{
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            //ui validations for email and password feild
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"enter email and password",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //all firebase logic + routing for the login
            AuthUtils.loginAndRoute(this,email,password)

        }



        //navigate to sign up flow
        signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }




}