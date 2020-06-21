package com.karan.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.karan.foodrunner.R
import com.karan.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_logged_in.*
import org.json.JSONObject

class Register : AppCompatActivity() {
    lateinit var name : EditText
    lateinit var email : EditText
    lateinit var mobile : EditText
    lateinit var address : EditText
    lateinit var pass : EditText
    lateinit var conpass : EditText
    lateinit var btnRegister: Button

    lateinit var sharedPreferences: SharedPreferences


    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    var mobilePattern = "[7-9][0-9]{9}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences = getSharedPreferences(
            getString(R.string.prefrence_file_name),
            Context.MODE_PRIVATE
        )

        name = findViewById(R.id.etName)
        email = findViewById(R.id.etEmail)
        mobile = findViewById(R.id.etMobileNumber)
        address = findViewById(R.id.etDeliveryAddress)
        pass = findViewById(R.id.etPassword)
        conpass = findViewById(R.id.etConfirmPassword)

        btnRegister = findViewById(R.id.btnRegister)


        btnRegister.setOnClickListener {

            if (name.text.toString().isEmpty())
                Toast.makeText(this@Register, "Enter Name", Toast.LENGTH_LONG).show()
            else if (email.text.toString().isEmpty())
                Toast.makeText(this@Register, "Enter Email Id", Toast.LENGTH_LONG).show()
            else if (mobile.text.toString().isEmpty())
                Toast.makeText(this@Register, "Enter Mobile Number", Toast.LENGTH_LONG).show()
            else if (address.text.toString().isEmpty())
                Toast.makeText(this@Register, "Enter Delivery Address", Toast.LENGTH_LONG).show()
            else if (pass.text.toString().isEmpty())
                Toast.makeText(this@Register, "Enter Password", Toast.LENGTH_LONG).show()
            else if (pass.text.toString() != conpass.text.toString())
                Toast.makeText(this@Register, "Passwords doesn't match. Please try again!", Toast.LENGTH_LONG).show()
            else if (!email.text.toString().trim().matches(emailPattern.toRegex()))
                Toast.makeText(this@Register, "Enter a valid Email Id", Toast.LENGTH_LONG).show()
            else if (!mobile.text.toString().trim().matches(mobilePattern.toRegex()))
                Toast.makeText(this@Register, "Enter a valid Mobile number", Toast.LENGTH_LONG).show()
            else if (pass.length() < 4) {
                Toast.makeText(this@Register, "Weak Password", Toast.LENGTH_LONG).show()
            } else {
                val url = "http://13.235.250.119/v2/register/fetch_result"
                val queue = Volley.newRequestQueue(this@Register)
                val jsonParams = JSONObject()
                jsonParams.put("name", name.text.toString())
                jsonParams.put("mobile_number", mobile.text.toString())
                jsonParams.put("password", pass.text.toString())
                jsonParams.put("address", address.text.toString())
                jsonParams.put("email", email.text.toString())

                if (ConnectionManager().checkConnectivity(this@Register)) {
                    val jsonObjectRequest = object : JsonObjectRequest(
                        Method.POST,
                        url,
                        jsonParams,
                        Response.Listener {

                            try {
                                val obj = it.getJSONObject("data")
                                val success = obj.getBoolean("success")
                                if (success) {
                                    val response = obj.getJSONObject("data")
                                    sharedPreferences.edit()
                                        .putString("user_id", response.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_name", response.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString(
                                            "user_mobile_number",
                                            response.getString("mobile_number")
                                        ).apply()
                                    sharedPreferences.edit()
                                        .putString("user_address", response.getString("address"))
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("user_email", response.getString("email"))
                                        .apply()

                                    sharedPreferences.edit()
                                        .putBoolean("isLoggedIn", true).apply()

                                    startActivity(
                                        Intent(this@Register, LoggedInActivity::class.java)
                                    )
                                    finish()
                                } else {
                                    //rlRegister.visibility= View.VISIBLE
                                    Toast.makeText(
                                        this@Register,
                                        "Some error occurred!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                //rlRegister.visibility= View.VISIBLE
                                e.printStackTrace()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(this@Register, "Volley Error!", Toast.LENGTH_SHORT)
                                .show()
                            //rlRegister.visibility= View.VISIBLE
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "9a5b7e4b6805f2"
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                } else {
                    val dialog = AlertDialog.Builder(this@Register)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Found")
                    dialog.setPositiveButton("Open Settings") { _, _ ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        this.finish()

                    }
                    dialog.setNegativeButton("Exit") { _, _ ->

                        ActivityCompat.finishAffinity(this@Register)
                    }
                    dialog.create()
                    dialog.show()
                }



                Toast.makeText(this@Register, "Successfully Registered", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(this@Register, LoggedInActivity::class.java)
                startActivity(intent)
            }
        }
    }


    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    LoginActivity::class.java
                )
            )
        }
    }
}
