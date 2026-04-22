package com.example.w1965221_finalyearproject.FirebaseFunc

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.TextView
import com.example.w1965221_finalyearproject.auth.LoginActivity
import com.example.w1965221_finalyearproject.client.Client
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//reusable helper object for user related fire store operations


object UserUtils{
    //loads the loggin in users full name from firestore and updates the welcome text

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val USER_COLLECTION = "user"

    fun loadUserName(welcomeText: TextView){


        //get the current user
        val currentUser = FirebaseAuth.getInstance().currentUser

        //safty check user should always exist
        if(currentUser == null){
            welcomeText.text = "welcome back!"
            return
        }

        val uid = currentUser.uid

        //fetch users firestore profile documents
        db.collection(USER_COLLECTION).document(uid).get()
            .addOnSuccessListener { document ->
                val fullName = document.getString("fullName")?: ""

                //update ui to show name
                welcomeText.text = "Welcome back, $fullName"
            }
            .addOnFailureListener{ e ->
                Log.e("FIRESTORE","Failed to load the user name", e)
                welcomeText.text = "Welcome back"
            }

    }

    //log out func logs out current user send them back to login page
    //remove repeated code
    fun logout(currentActivity: Activity){
        //sign out from firebase authentication
        FirebaseAuth.getInstance().signOut()

        //create intent to go back to login screen
        val intent = Intent(currentActivity, LoginActivity::class.java)

        //clear back stack so user cannot press back to dashboard
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        currentActivity.startActivity(intent)
        //finish current activity
        currentActivity.finish()
    }


    //load client calories and macros to the overview on dashboard
    fun loadClientOverview(overviewTextView: TextView){
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null){
            overviewTextView.text = "Calories: -\nProtein: -\nCarbs: -\nFats: "
            return
        }

        val uid = currentUser.uid
    // readinf from the firestroe
        db.collection(USER_COLLECTION).document(uid).get()
            //when loaded sucessfuly
            .addOnSuccessListener { document ->
                //even if value is saved as int firestore stores numbers internally as
                //long
                val calories = document.getLong("targetCalories")?.toInt()
                val protein = document.getLong("targetProtein")?.toInt()
                val carbs = document.getLong("targetCarbs")?.toInt()
                val fats = document.getLong("targetFats")?.toInt()

                val caloriesText = calories?.let { "$it kcal" }?:"-"// formating ui if null -> "-"
                val proteinText = protein?.let { "${it}g" }?:"-"
                val carbsText = carbs?.let { "${it}g" }?:"-"
                val fatsText = fats?.let { "${it}g" }?:"-"

                overviewTextView.text =
                    "Calories: $caloriesText\n"+ "Protein: $proteinText\n"+"Carbs: $carbsText\n"+
                            "Fats: $fatsText"

            }
            .addOnFailureListener{
                overviewTextView.text = "Calories: -\nProtein: -\nCarbs: -\nFats: "
            }
    }

    //load coach code for currently logged in coach
    fun loadCoachCode(
        onSuccess: (String?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        db.collection(USER_COLLECTION).document(uid).get()
            .addOnSuccessListener { document ->
                onSuccess(document.getString("coachCode"))
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }


    //client enters coach code
    //if the code matches a coach
    // save linkedCoachId+ linkedCoachcode on client doc
    //add this client inside coach/uid/clients
    fun linkClientToCoach(
        coachCode: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val clientUser = FirebaseAuth.getInstance().currentUser
        if (clientUser == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        val clientUid = clientUser.uid

        //  load current client profile
        db.collection(USER_COLLECTION).document(clientUid).get()
            .addOnSuccessListener { clientDoc ->
                val clientName = clientDoc.getString("fullName") ?: "Client"
                val goalType = clientDoc.getString("goalType") ?: "No goal"

                //  find coach by code
                db.collection(USER_COLLECTION)
                    .whereEqualTo("role", "coach")
                    .whereEqualTo("coachCode", coachCode)
                    .get()
                    .addOnSuccessListener { result ->
                        val coachDoc = result.documents.firstOrNull()

                        if (coachDoc == null) {
                            onFailure(Exception("Invalid coach code"))
                            return@addOnSuccessListener
                        }

                        val coachUid = coachDoc.id

                        //update client document with coach link
                        val clientUpdates = hashMapOf<String, Any>(
                            "linkedCoachId" to coachUid,
                            "linkedCoachCode" to coachCode
                        )

                        db.collection(USER_COLLECTION).document(clientUid)
                            .update(clientUpdates)
                            .addOnSuccessListener {
                                // Step 4: add client under coach's clients subcollection
                                val coachClientData = hashMapOf(
                                    "clientName" to clientName,
                                    "goalType" to goalType
                                )

                                db.collection(USER_COLLECTION)
                                    .document(coachUid)
                                    .collection("clients")
                                    .document(clientUid)
                                    .set(coachClientData)
                                    .addOnSuccessListener { onSuccess() }
                                    .addOnFailureListener { e -> onFailure(e) }
                            }
                            .addOnFailureListener { e ->
                                onFailure(e)
                            }
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }


    //load real linked clients for the currently logged in coach
    fun loadLinkedCoachCode(
        onSuccess: (String?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            onFailure(Exception("User not logged in"))
            return
        }

        db.collection(USER_COLLECTION).document(uid).get()
            .addOnSuccessListener { document ->
                onSuccess(document.getString("linkedCoachCode"))
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }


     // Load real linked clients for the currently logged in coach.
     fun loadCoachClients(
         onSuccess: (List<Client>) -> Unit,
         onFailure: (Exception) -> Unit
     ) {
         val coachUid = FirebaseAuth.getInstance().currentUser?.uid
         if (coachUid == null) {
             onFailure(Exception("User not logged in"))
             return
         }

         db.collection(USER_COLLECTION)
             .document(coachUid)
             .collection("clients")
             .get()
             .addOnSuccessListener { result ->
                 val clients = result.documents.map { doc ->
                     Client(
                         uid = doc.id,
                         name = doc.getString("clientName") ?: "Client",
                         goal = doc.getString("goalType") ?: "No goal",
                         status = "Linked"
                     )
                 }
                 onSuccess(clients)
             }
             .addOnFailureListener { e ->
                 onFailure(e)
             }
     }

    //load clients real summary from the main user document
    //used by coach client detail screen
    fun loadClientSummary(
        clientUid: String,
        onSuccess: (String,String) -> Unit,
        onFailure: (Exception) -> Unit
    ){
        db.collection(USER_COLLECTION).document(clientUid).get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("fullName") ?: "Client"

                val calories = (doc.get("targetCalories") as? Number)?.toInt()
                val protein = (doc.get("targetProtein") as? Number)?.toInt()
                val carbs = (doc.get("targetCarbs") as? Number)?.toInt()
                val fats = (doc.get("targetFats") as? Number)?.toInt()
                val goalType = doc.getString("goalType") ?: "-"
                val selectedProgramId = doc.getString("selectedProgramId") ?: "-"

                val summary =
                    "Goal: $goalType\n" +
                            "Program: $selectedProgramId\n" +
                            "Calories: ${calories ?: "-"}\n" +
                            "Protein: ${protein ?: "-"}g\n" +
                            "Carbs: ${carbs ?: "-"}g\n" +
                            "Fats: ${fats ?: "-"}g"

                onSuccess(name, summary)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    //coach updates a clients macro target
    //coah edits protein, fats and carbs
    //cal are auto recalculated from those macros
    //this works regardless if the client uses auto or manaual mode
    //firebase fields updated are
    //targetProtein
    //targetCarbs
    //targetFats
    //targetCalories
    fun updateClientMacros(
        clientUid: String,
        protein:Int,
        carbs:Int,
        fats: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        //recalc calories from macors
        val calories  = (protein*4)+(carbs*4)+(fats*9)

        val updates = hashMapOf<String, Any>(
            "targetProtein" to protein,
            "targetCarbs" to carbs,
            "targetFats" to fats,
            "targetCalories" to calories
        )

        db.collection(USER_COLLECTION)
            .document(clientUid)
            .update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener{e -> onFailure(e)}
    }

    //load number of real linked clinets for the current coach
    //firebase path
    //user/coachuid/clients
    //the number shown on the dashboard is the nmber of documents
    //inside the subcollection
    fun loadCoachClientCount(
        onSuccess: (Int) -> Unit,
        onFailure: (Exception) -> Unit
    ){
        val coachUid = FirebaseAuth.getInstance().currentUser?.uid
        if (coachUid == null){
            onFailure(Exception("User not logged in"))
            return
        }

        db.collection(USER_COLLECTION)
            .document(coachUid)
            .collection("clients")
            .get()
            .addOnSuccessListener { result ->
                onSuccess(result.size())
            }
            .addOnFailureListener{e ->
                onFailure(e)
            }

    }



}