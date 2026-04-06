package com.example.w1965221_finalyearproject.coach

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.client.Client
import com.example.w1965221_finalyearproject.FirebaseFunc.UserUtils

//master list of clients assigned to caoch
//clicking client opens detials
// master list of real clients linked to the coach
class ClientListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerClients)
        recyclerView.layoutManager = LinearLayoutManager(this)

        UserUtils.loadCoachClients(
            onSuccess = { clients ->
                recyclerView.adapter = ClientAdapter(clients) { client ->
                    val intent = android.content.Intent(this, ClientDetailActivity::class.java)
                    intent.putExtra("client_uid", client.uid)
                    intent.putExtra("client_name", client.name)
                    startActivity(intent)
                }
            },
            onFailure = { e ->
                Toast.makeText(this, "Failed to load clients: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }
}