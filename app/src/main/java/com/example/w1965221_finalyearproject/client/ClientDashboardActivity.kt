package com.example.w1965221_finalyearproject.client

import android.content.Intent
import android.os.Bundle

import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.w1965221_finalyearproject.FirebaseFunc.UserUtils
import com.google.firebase.firestore.auth.User


//client home screen / nav hub
//acts central menu for client features(training , nutrion and progress)
class ClientDashboardActivity : AppCompatActivity(){



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //show the xml screen
        setContentView(R.layout.activity_client_dashboard)

        val overviewText = findViewById<TextView>(R.id.tvOverView)

        //welcome text to see the user who logs in
        val welcomeText = findViewById<TextView>(R.id.tvWelcome)
        UserUtils.loadUserName(welcomeText)

        //find logout button from xml
        val logoutButton = findViewById<Button>(R.id.btnLogout)
        logoutButton.setOnClickListener{
            UserUtils.logout(this)
        }

        UserUtils.loadClientOverview(overviewText)



        //nav to training plan recyler based excerise list
        findViewById<Button>(R.id.btnTrainingPlan).setOnClickListener {
            startActivity(Intent(this, TrainingPlanActivity::class.java))
        }
        //nav to macros and login
        findViewById<Button>(R.id.btnMacros).setOnClickListener {
            startActivity(Intent(this, MacrosActivity::class.java))
        }

        // nav to weight progress and tracking graph
        findViewById<Button>(R.id.btnWeight).setOnClickListener {
            startActivity(Intent(this, WeightProgressActivity::class.java))
        }
    }


}