package com.example.w1965221_finalyearproject.client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R


//client calibration
//collects data for the client to workout marcos
//and displays it to the client dashboard
class ClientCalibrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_calibration)

        val finishbutton = findViewById<Button>(R.id.btnFinishSetup)
        val skipText = findViewById<TextView>(R.id.tvSkip)

        //nav to dashboard
        finishbutton.setOnClickListener{
            startActivity(Intent(this, ClientDashboardActivity::class.java))
            finish()
        }

        skipText.setOnClickListener{
            startActivity(Intent(this,ClientDashboardActivity::class.java))
        }
    }

}