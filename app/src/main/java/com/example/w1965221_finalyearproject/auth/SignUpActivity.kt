package com.example.w1965221_finalyearproject.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.client.ClientDashboardActivity
import com.example.w1965221_finalyearproject.coach.CoachDashboardActivity


//sign up screen
//shows role based routing client nd coach
//later collect user details, create account and persist role to data base
class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //draw the xml
        setContentView(R.layout.activity_signup)

        //give values to the buytton for event listener
        val roleGroup = findViewById<RadioGroup>(R.id.roleGroup)
        val createAccountButton = findViewById<Button>(R.id.btnCreateAccount)

        //role selected determines whcih dashboard opens
        createAccountButton.setOnClickListener {

            when (roleGroup.checkedRadioButtonId) {

                R.id.radioClient -> {
                    startActivity(
                        Intent(this, ClientDashboardActivity::class.java)
                    )
                }

                R.id.radioCoach -> {
                    startActivity(
                        Intent(this, CoachDashboardActivity::class.java)
                    )
                }
            }
        }
    }

}