package com.example.w1965221_finalyearproject.coach

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R

class CoachDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coach_dashboard)

        findViewById<Button>(R.id.btnViewClients).setOnClickListener {
            startActivity(Intent(this, ClientListActivity::class.java))
        }

        findViewById<Button>(R.id.btnCreateProgram).setOnClickListener {
            startActivity(Intent(this, AdjustProgramActivity::class.java))
        }
    }
}
