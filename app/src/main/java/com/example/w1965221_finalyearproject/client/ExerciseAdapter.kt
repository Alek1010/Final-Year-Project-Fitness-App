package com.example.w1965221_finalyearproject.client

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.w1965221_finalyearproject.R


//recycler view adapts responsible for displaying each excerise in a reusable card layout
//the adaptor inflates item_excercise.xml and binds excerise data into it
// by its self just empty container
//1 create card inflate xml layout
//2 fill each card with the correct data
//training pkans are list of excerises recyclerview + adapter good for showing rusable cards
class ExerciseAdapter : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        throw NotImplementedError("ExerciseAdapter is not used in the current training plan screen")
    }

    override fun getItemCount(): Int = 0

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {}
}