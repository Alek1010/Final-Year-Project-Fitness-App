package com.example.w1965221_finalyearproject.client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R

//client home screen / nav hub
//acts central menu for client features(training , nutrion and progress)
class ClientDashboardActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //show the xml screen
        setContentView(R.layout.activity_client_dashboard)
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