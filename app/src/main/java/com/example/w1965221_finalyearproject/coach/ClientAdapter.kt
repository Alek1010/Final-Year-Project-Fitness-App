package com.example.w1965221_finalyearproject.coach

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.client.Client
//adapter for displaing coaches client list
//uses item_client. xml as rusable csrd to display
//similar to excerise card
class ClientAdapter(
    private val clients: List<Client>,
    private val onClientClick: (Client) -> Unit
) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    //holds references to one client card
    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvClientName)
        val goal: TextView = itemView.findViewById(R.id.tvClientGoal)
        val status: TextView = itemView.findViewById(R.id.tvClientStatus)
    }
    //when andoird needs a card the adaptor infplates item_client.xml
    //wraps it  in a view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_client, parent, false)
        return ClientViewHolder(view)
    }
    //bind the data to the card and handle when the card is clicked
    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]

        holder.name.text = client.name
        holder.goal.text = "Goal: ${client.goal}"
        holder.status.text = "Status: ${client.status}"

        holder.itemView.setOnClickListener {
            onClientClick(client)
        }
    }
    //keep count of items to display
    override fun getItemCount(): Int = clients.size
}
