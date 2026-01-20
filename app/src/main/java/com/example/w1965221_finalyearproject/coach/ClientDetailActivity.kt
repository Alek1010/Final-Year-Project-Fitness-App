package com.example.w1965221_finalyearproject.coach

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
//for adjusting the clients program
class ClientDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_detail)

        val clientName = intent.getStringExtra("client_name")

        findViewById<TextView>(R.id.tvClientName).text =
            clientName ?: "Client"
    }
}