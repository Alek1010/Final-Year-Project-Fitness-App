package com.example.w1965221_finalyearproject.auth


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.w1965221_finalyearproject.R
import com.google.firebase.auth.FirebaseAuth

import com.example.w1965221_finalyearproject.FirebaseFunc.SignUpUtils

//sign up screen
//shows role based routing client nd coach
//later collect user details, create account and persist role to data base
class SignUpActivity : AppCompatActivity() {

    //firebase auth for account creation
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //draw the xml
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        val emailInput =findViewById<EditText>(R.id.etEmail)
        val passwordInput = findViewById<EditText>(R.id.etPassword)
        val createButton = findViewById<Button>(R.id.btnCreateAccount)
        val name = findViewById<EditText>(R.id.etName)
        val roleGroup = findViewById<RadioGroup>(R.id.roleGroup)


        createButton.setOnClickListener{
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val fullName = name.text.toString().trim()
            val selectedRoleId = roleGroup.checkedRadioButtonId

            //basic validation before calling firebase
            if(fullName.isEmpty()|| email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"enter name, email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //fire bases requires password lenght >= 6
            if(password.length <6){
                Toast.makeText(this, "password must be at least 6 character",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //validate role is selcted
            if(selectedRoleId == -1){
                Toast.makeText(this,"please select a role", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //read selected roles
            val role = when(roleGroup.checkedRadioButtonId){
                R.id.radioCoach -> "coach"
                R.id.radioClient ->"client"
                else -> {
                    Toast.makeText(this,"invalid role selcted", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            SignUpUtils.creatAccount(
                activity = this,
                fullName = fullName,
                email = email,
                password = password,
                role = role
            )




        }




    }

}