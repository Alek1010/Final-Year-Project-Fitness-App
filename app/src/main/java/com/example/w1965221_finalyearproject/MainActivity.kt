package com.example.w1965221_finalyearproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.auth.LoginActivity

//main entry point launched by the android launcher
//this activity acts as a lightweight router to the authentication flow
//keeping routing seperate makes navigation easier to change later e.g(auto log in)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        android.util.Log.d("FYP", "FirebaseAuth ready: ${auth.currentUser}")

        // Launch Login screen and close it so it doesnt appear in the background
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
