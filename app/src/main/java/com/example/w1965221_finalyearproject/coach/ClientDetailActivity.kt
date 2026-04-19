package com.example.w1965221_finalyearproject.coach

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import com.example.w1965221_finalyearproject.FirebaseFunc.UserUtils
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import android.content.Intent
import com.example.w1965221_finalyearproject.client.WeightProgressActivity
import com.example.w1965221_finalyearproject.client.TrainingPlanActivity

//for adjusting the clients program

//allows coach to
//view a linked clients current summary
//edit clients macros
//coach imports protein, fat and carbs
// calories. = protien *4 +carbs*4 + fats*9
//works regardless of how the client set up their account weather it
//is auto or manual
class ClientDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_detail)

        val clientUid = intent.getStringExtra("client_uid")
        val tvClientName = findViewById<TextView>(R.id.tvClientName)
        val tvClientSummary = findViewById<TextView>(R.id.tvClientSummary)

        val etCoachProtein = findViewById<EditText>(R.id.etCoachProtein)
        val etCoachCarbs = findViewById<EditText>(R.id.etCoachCarbs)
        val etCoachFats = findViewById<EditText>(R.id.etCoachFats)
        val btnSaveClientMacros = findViewById<Button>(R.id.btnSaveClientMacros)

        val btnViewWeightProgress = findViewById<Button>(R.id.btnViewWeight)
        val btnAdjustTrainingProgram = findViewById<Button>(R.id.btnAdjustTraining)

        if (clientUid.isNullOrEmpty()) {
            tvClientName.text = "Client"
            tvClientSummary.text = "No client data found"
            return
        }

        //load cliuent summary from firebase
        loadClientData(
            clientUid = clientUid,
            tvClientName = tvClientName,
            tvClientSummary = tvClientSummary
        )

        //save macro button
        //validate then update clients target in firebase
        btnSaveClientMacros.setOnClickListener {
            val protein = etCoachProtein.text.toString().toIntOrNull()
            val carbs = etCoachCarbs.text.toString().toIntOrNull()
            val fats = etCoachFats.text.toString().toIntOrNull()

            if (protein == null || carbs == null || fats == null) {
                Toast.makeText(this, "Enter valid protein, carbs, and fats", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }


            UserUtils.updateClientMacros(
                clientUid = clientUid,
                protein = protein,
                carbs = carbs,
                fats = fats,
                onSuccess = {
                    Toast.makeText(this, "Client macros updated", Toast.LENGTH_SHORT).show()

                    // Clear fields after saving
                    etCoachProtein.text.clear()
                    etCoachCarbs.text.clear()
                    etCoachFats.text.clear()

                    // Reload client summary so coach sees updated values immediately
                    loadClientData(
                        clientUid = clientUid,
                        tvClientName = tvClientName,
                        tvClientSummary = tvClientSummary
                    )
                }, onFailure = { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }

        //check client weight progress button
        btnViewWeightProgress.setOnClickListener{
            val intent = Intent(this,WeightProgressActivity::class.java)
            intent.putExtra("client_uid",clientUid)
            startActivity(intent)
        }

        //adjust and change the client training program
        btnAdjustTrainingProgram.setOnClickListener{
            val intent = Intent(this,TrainingPlanActivity::class.java)
            intent.putExtra("client_uid",clientUid)
            startActivity(intent)
        }

    }

    //load current cliuent summary from firebase and display it on screen
    private fun loadClientData(
        clientUid: String,
        tvClientName: TextView,
        tvClientSummary: TextView
    ) {
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





