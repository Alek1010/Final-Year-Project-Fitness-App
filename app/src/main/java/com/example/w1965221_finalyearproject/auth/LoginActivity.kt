package com.example.w1965221_finalyearproject.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.client.ClientDashboardActivity



class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.btnLogin)
        val signUpText = findViewById<TextView>(R.id.tvSignUp)

        // TEMP: login as client
        loginButton.setOnClickListener {
            startActivity(Intent(this, ClientDashboardActivity::class.java))
        }

        signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}