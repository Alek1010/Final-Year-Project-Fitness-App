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
class ExerciseAdapter(private val exercises: List<Exercise>)
    //adaptor for recycler view
    : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>(){

        //holds reference to the views inside one excerise card
        //recyclerview reuses these holders as the user scrolls
        // keeps performance smooth
    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvExerciseName)
        val muscle: TextView = itemView.findViewById(R.id.tvMuscleGroup)
        val sets: TextView = itemView.findViewById(R.id.tvSets)
        val logButton: Button = itemView.findViewById(R.id.btnLogSet)
    }
    //called when recycler view needs new card to display
    //inflate the item_excercise.xml into a real view object
    //then wrap the view in a viewholder so we can acess its inner view easily
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        //inflate a new card when recyclerview needs a row view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    //tells recylerview how many items exist in the list
    //so it knows how many rows to expect and where the list ends
    // if it is 0 nothibng will appear
    override fun getItemCount(): Int = exercises.size

    //called when we want to display data at a certain position
    //position is the indext in the exercise list
    //here the object is taken and put into the ui card
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        // fill ui element in the card with the data
        holder.name.text = exercise.name
        holder.muscle.text = exercise.muscleGroup
        holder.sets.text = exercise.sets
        //contextual loggin logs the excerise via dialog
        // clicking log opens dialog specific to that excersie to log what was performed how many
        //reps and sets
        holder.logButton.setOnClickListener {
            showLogDialog(holder.itemView.context, exercise.name)
        }
    }

    //creat and display pop up where user can input weight and reps
    //uses activity context to attach to the screen
    //dialog_log_set.xml inflated
    //whne saved pressed values read and show a toast

    //right now only shows a toast proof of working
    //later validate inputs
    //store the log in room/firebase
    //update the card to show status
    private fun showLogDialog(context: android.content.Context, exerciseName: String) {
        //inflate dialog ui from xml
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_log_set, null)

        //grab input fields
        val weightInput = dialogView.findViewById<EditText>(R.id.etWeight)
        val repsInput = dialogView.findViewById<EditText>(R.id.etReps)

        //build and show the screen
        AlertDialog.Builder(context)
            .setTitle("Log $exerciseName")//show exercise
            .setView(dialogView)//set custom dialog layoyt as the content
            .setPositiveButton("Save") { _, _ ->
                val weight = weightInput.text.toString()
                val reps = repsInput.text.toString()
                //when saved read the input and confirm witgh toast
                Toast.makeText(
                    context,
                    "Logged: $weight kg × $reps reps",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel", null)// close if no action
            .show()
    }

}