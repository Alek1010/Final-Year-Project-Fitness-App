package com.example.w1965221_finalyearproject.client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R

class ClientDashboardActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_dashboard)

        findViewById<Button>(R.id.btnTrainingPlan).setOnClickListener {
            startActivity(Intent(this, TrainingPlanActivity::class.java))
        }

        findViewById<Button>(R.id.btnMacros).setOnClickListener {
            startActivity(Intent(this, MacrosActivity::class.java))
        }


        findViewById<Button>(R.id.btnWeight).setOnClickListener {
            startActivity(Intent(this, WeightProgressActivity::class.java))
        }
    }

}