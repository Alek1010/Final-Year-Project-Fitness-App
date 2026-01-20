package com.example.w1965221_finalyearproject.coach

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.client.Client

class ClientListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerClients)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val fakeClients = listOf(
            Client("Alex Johnson", "Fat Loss", "On track"),
            Client("Sarah Lee", "Muscle Gain", "Needs adjustment"),
            Client("Tom Brown", "Recomposition", "On track"),
            Client("Emily Davis", "Strength", "Behind target")
        )
        recyclerView.adapter = ClientAdapter(fakeClients) { client ->
            val intent = android.content.Intent(this, ClientDetailActivity::class.java)
            intent.putExtra("client_name", client.name)
            startActivity(intent)
        }
    }
}