package com.example.w1965221_finalyearproject.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.client.ClientDashboardActivity


//login screen
//currently routs directly to client dash board as placeholder
//later need to validate email/password and authenticate via firebase
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inflates res/layout/activity_login.xml to draw the screen
        setContentView(R.layout.activity_login)

        //gives value to the button in the xml so event listen works
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val signUpText = findViewById<TextView>(R.id.tvSignUp)

        // TEMP: login as client assuems successful login
        loginButton.setOnClickListener {
            startActivity(Intent(this, ClientDashboardActivity::class.java))
        }

        //navigate to sign up flow
        signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}