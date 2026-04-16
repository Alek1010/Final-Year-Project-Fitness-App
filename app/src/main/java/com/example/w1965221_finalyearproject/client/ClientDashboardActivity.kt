package com.example.w1965221_finalyearproject.client

import android.content.Intent
import android.os.Bundle

import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.FirebaseFunc.UserUtils
import android.app.AlertDialog
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast

//client home screen / nav hub
//acts central menu for client features(training , nutrion and progress)
class ClientDashboardActivity : AppCompatActivity(){



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //show the xml screen
        setContentView(R.layout.activity_client_dashboard)

        val overviewText = findViewById<TextView>(R.id.tvOverView)
        val btnProfile = findViewById<ImageButton>(R.id.btnProfile)
        //welcome text to see the user who logs in
        val welcomeText = findViewById<TextView>(R.id.tvWelcome)
        val tvCoachLinkStatus = findViewById<TextView>(R.id.tvCoachLinkStatus)
        UserUtils.loadUserName(welcomeText)

        //find logout button from xml
        val logoutButton = findViewById<Button>(R.id.btnLogout)
        logoutButton.setOnClickListener{
            UserUtils.logout(this)
        }

        UserUtils.loadClientOverview(overviewText)

        //show weather coach is linked
        UserUtils.loadLinkedCoachCode(
            onSuccess = {code ->
                tvCoachLinkStatus.text =
                    if (code.isNullOrEmpty()) {"no coach linked"}
                    else {" linked coach code: $code"}
            },
            onFailure = {
                tvCoachLinkStatus.text = "no coach Linked"
            }
        )

        //link coach button
        findViewById<Button>(R.id.btnLinkCoach).setOnClickListener {
            val input = EditText(this)
            input.hint = "Enter coach code"

            AlertDialog.Builder(this)
                .setTitle("Link to Coach")
                .setView(input)
                .setPositiveButton("Link") { _, _ ->
                    val coachCode = input.text.toString().trim().uppercase()

                    if (coachCode.isEmpty()) {
                        Toast.makeText(this, "Enter a coach code", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    UserUtils.linkClientToCoach(
                        coachCode = coachCode,
                        onSuccess = {
                            Toast.makeText(this, "Coach linked successfully", Toast.LENGTH_SHORT).show()
                            tvCoachLinkStatus.text = "Linked coach code: $coachCode"
                        },
                        onFailure = { e ->
                            Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }
                .setNegativeButton("Cancel", null)
                .show()
        }



        //navigate back to calibration page
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ClientCalibrationActivity::class.java))
        }

        //nav to training plan
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