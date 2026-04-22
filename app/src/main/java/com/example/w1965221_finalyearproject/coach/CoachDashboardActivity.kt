package com.example.w1965221_finalyearproject.coach


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.FirebaseFunc.UserUtils



//coach home screen
//provides acess to client managment and future program adjustment
class CoachDashboardActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coach_dashboard)

        val welcomeText = findViewById<TextView>(R.id.tvWelcome)
        val coachCodeText = findViewById<TextView>(R.id.tvCoachCode)
        val tvOverview = findViewById<TextView>(R.id.tvOverview)

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

        //add in the current number of active clients
        UserUtils.loadCoachClientCount(
            onSuccess = {count ->
                tvOverview.text = "current linked clients: $count"
            },
            onFailure = {
                tvOverview.text = "current linked clients: -"
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


    }
}
