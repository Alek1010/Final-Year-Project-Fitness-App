package com.example.w1965221_finalyearproject.coach

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.client.Client
//master list of clients assigned to caoch
//clicking client opens detials
class ClientListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //open the xml
        setContentView(R.layout.activity_client_list)
        //config view vertical scrolling
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerClients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        //filler data
        val fakeClients = listOf(
            Client("Alex Johnson", "Fat Loss", "On track"),
            Client("Sarah Lee", "Muscle Gain", "Needs adjustment"),
            Client("Tom Brown", "Recomposition", "On track"),
            Client("Emily Davis", "Strength", "Behind target")
        )
        //this inflartes the client card xml
        //puts the fake filler clints to one card ruses the card as the user scrolls
        //puts them onto the clinet list xml
        recyclerView.adapter = ClientAdapter(fakeClients) { client ->
            val intent = android.content.Intent(this, ClientDetailActivity::class.java)
            intent.putExtra("client_name", client.name)
            startActivity(intent)
        }
    }
}