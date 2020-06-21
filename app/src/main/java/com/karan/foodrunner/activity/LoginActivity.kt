package com.karan.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.karan.foodrunner.R
import com.karan.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class LoginActivity : AppCompatActivity(){

    lateinit var etMobileNumber: EditText
    lateinit var etPassword : EditText
    lateinit var btnLogin : Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtRegister : TextView

    lateinit var sharedPreferences: SharedPreferences

    var mobilePattern = "[7-9][0-9]{9}"


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


        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPassword::class.java)
            startActivity(intent)
        }


        txtRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, Register::class.java)
            startActivity(intent)
        }


        btnLogin.setOnClickListener {
            val mobileNumber= etMobileNumber.text.toString()
            val password = etPassword.text.toString()

            if(validations(mobileNumber,password)) {

                if (ConnectionManager().checkConnectivity(this@LoginActivity)) {

                    val queue = Volley.newRequestQueue(this@LoginActivity)
                    val url = "http://13.235.250.119/v2/login/fetch_result/"
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", mobileNumber)
                    jsonParams.put("password", password)

                    val jsonObjectRequest =
                        object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    btnLogin.isEnabled = false
                                    btnLogin.isClickable = false

                                    val response = data.getJSONObject("data")
                                    sharedPreferences.edit()
                                        .putString("user_id", response.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_name", response.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_mobile_number", response.getString("mobile_number")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_address", response.getString("address")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_email", response.getString("email")).apply()

                                    sharedPreferences.edit()
                                        .putBoolean("isLoggedIn", true).apply()


                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            LoggedInActivity::class.java
                                        )
                                    )
                                    finish()
                                } else
                                {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Invalid Credentials",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@LoginActivity,
                                "Volley error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "9a5b7e4b6805f2"
                                return headers
                            }
                        }
                    queue.add(jsonObjectRequest)
                }else
                {
                    val dialog= AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings"){ _, _ ->
                        val settingsIntent=Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        this.finish()
                    }
                    dialog.setNegativeButton("Exit"){ _, _ ->
                        ActivityCompat.finishAffinity(this@LoginActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }

        }


    }


    private fun validations(phone:String,password:String):Boolean {

        if (phone.isEmpty()) {
            Toast.makeText(this@LoginActivity, "Enter Mobile Number", Toast.LENGTH_LONG).show()
            return false
        } else if(password.isEmpty()) {
            Toast.makeText(this@LoginActivity, "Enter Password", Toast.LENGTH_LONG).show()
            return false
        }else if(!phone.trim().matches(mobilePattern.toRegex())) {
            Toast.makeText(
                this@LoginActivity,
                "Enter a valid Mobile number",
                Toast.LENGTH_LONG
            )
                .show()
            return false
        }else{
            return true
        }


    }




}


