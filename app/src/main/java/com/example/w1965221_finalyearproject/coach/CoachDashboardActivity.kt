package com.example.w1965221_finalyearproject.coach


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

//coach home screen
//provides acess to client managment and future program adjustment
class CoachDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coach_dashboard)

        //find logout button from xml
        val logoutButton = findViewById<Button>(R.id.btLogout)
        logoutButton.setOnClickListener{
            //sign user out of firebase
            FirebaseAuth.getInstance().signOut()
            //NAV back to login screen
            val intent = Intent(this, LoginActivity::class.java)
            //clear activity stack so user cannot press back to dashbaord
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            //finish current activity
            finish()
        }

        findViewById<Button>(R.id.btnViewClients).setOnClickListener {
            startActivity(Intent(this, ClientListActivity::class.java))
        }

        findViewById<Button>(R.id.btnCreateProgram).setOnClickListener {
            startActivity(Intent(this, AdjustProgramActivity::class.java))
        }
    }
}
