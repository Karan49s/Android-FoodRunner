package com.karan.foodrunner

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity(){

    lateinit var etMobileNumber: EditText
    lateinit var etPassword : EditText
    lateinit var btnLogin : Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtRegister : TextView

    lateinit var sharedPreferences: SharedPreferences

    val validMobileNumber="1111111111"
    val validPassword ="11111"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =getSharedPreferences(getString(R.string.prefrence_file_name),
            Context.MODE_PRIVATE)

        val isLoggedIn= sharedPreferences.getBoolean("isLoggedIn",false)
        setContentView(R.layout.activity_login)

        if(isLoggedIn) {
            val intent = Intent(this@LoginActivity, LoggedInActivity::class.java)
            startActivity(intent)
            finish()
        }




        title = "Log in"

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegister = findViewById(R.id.txtRegister)



        btnLogin.setOnClickListener {
            val mobileNumber= etMobileNumber.text.toString()
            val password = etPassword.text.toString()

            if((mobileNumber==validMobileNumber) && password==validPassword)
            {
                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                sharedPreferences.edit().putString("mobile",mobileNumber).apply()
                val intent = Intent(this@LoginActivity, LoggedInActivity::class.java)

                startActivity(intent)
                finish()
            }
            else
            {
                Toast.makeText(this@LoginActivity,"Incorrect credentials",Toast.LENGTH_LONG).show()
            }

        }

        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPassword::class.java)
            startActivity(intent)
        }


        txtRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, Register::class.java)
            startActivity(intent)
        }
    }




}


