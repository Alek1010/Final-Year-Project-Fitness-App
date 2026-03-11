package com.example.w1965221_finalyearproject.FirebaseFunc
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


//macrosUtils
//handles saving and loading daily macros log for the current user
//each day is stored as one document using the data string as the document id
object MacroLogUtils {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private const val USER_COLLECTION = "user"
    private const val MACROS_LOGS_COLLECTION ="macroLogs"

    //save or update the macro log for one day
    //because the document ID is the data string saving same date again
    //overwrites the days values instead of creating duplicates
    fun saveMacroLog(
        dateId: String,
        calories: Int,
        protein: Int,
        carbs: Int,
        fats: Int,
        water: Double,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        val currentUser = auth.currentUser
        if(currentUser == null){
            onFailure(Exception("no logged-in user found"))
            return
        }

        val uid = currentUser.uid

        val macroDoc = hashMapOf(
            "logDate" to dateId,
            "calories" to calories,
            "protein" to protein,
            "carbs" to carbs,
            "fats" to fats,
            "water" to water
        )

        db.collection(USER_COLLECTION)
            .document(uid)
            .collection(MACROS_LOGS_COLLECTION)
            .document(dateId)
            .set(macroDoc)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener{ e->
                Log.e("FIRESTORE", "Failed to save marco log", e)
                onFailure(e)
            }
    }

    //load the macro log for one data
    //if the data has no saved log return null values through the call back
    fun loadMacroLog(
        dateId: String,
        onSuccess: (Map<String, Any>?) -> Unit,
        onFailure: (Exception) -> Unit
    ){
        val currentUser = auth.currentUser
        if(currentUser == null){
            onFailure(Exception("no logged in user found"))
            return
        }

        val uid = currentUser.uid

        db.collection(USER_COLLECTION)
            .document(uid)
            .collection(MACROS_LOGS_COLLECTION)
            .document(dateId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()){
                    onSuccess(document.data)
                }else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener{ e ->
                Log.e("FIRESTORE","Failed to load macro log", e)
                onFailure(e)
            }

    }


}