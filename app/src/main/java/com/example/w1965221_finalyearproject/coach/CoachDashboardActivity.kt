package com.example.w1965221_finalyearproject.coach


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
import kotlin.math.log


//coach home screen
//provides acess to client managment and future program adjustment
class CoachDashboardActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coach_dashboard)

        val welcomeText = findViewById<TextView>(R.id.tvWelcome)
        val coachCodeText = findViewById<TextView>(R.id.tvCoachCode)

        UserUtils.loadUserName(welcomeText)

        //load coach code from the firebase so coach can share it with client
        UserUtils.loadCoachCode(
            onSuccess = {code ->
                coachCodeText.text = "your coach code: ${code ?: "-"}"
            },
            onFailure = {
                coachCodeText.text = "Your coach code: -"
            }
        )

        //find logout button from xml
        val logoutButton = findViewById<Button>(R.id.btnLogout)
        logoutButton.setOnClickListener{
            UserUtils.logout(this)
        }


        findViewById<Button>(R.id.btnViewClients).setOnClickListener {
            startActivity(Intent(this, ClientListActivity::class.java))
        }

        findViewById<Button>(R.id.btnCreateProgram).setOnClickListener {
            startActivity(Intent(this, AdjustProgramActivity::class.java))
        }
    }
}
