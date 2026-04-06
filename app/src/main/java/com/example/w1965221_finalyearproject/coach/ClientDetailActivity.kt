package com.example.w1965221_finalyearproject.coach

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.FirebaseFunc.UserUtils
//for adjusting the clients program

class ClientDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_detail)

        val clientUid = intent.getStringExtra("client_uid")
        val tvClientName = findViewById<TextView>(R.id.tvClientName)
        val tvClientSummary = findViewById<TextView>(R.id.tvClientSummary)

        if (clientUid.isNullOrEmpty()) {
            tvClientName.text = "Client"
            tvClientSummary.text = "No client data found"
            return
        }

        UserUtils.loadClientSummary(
            clientUid = clientUid,
            onSuccess = { name, summary ->
                tvClientName.text = name
                tvClientSummary.text = summary
            },
            onFailure = {
                tvClientName.text = "Client"
                tvClientSummary.text = "Failed to load client data"
            }
        )
    }
}