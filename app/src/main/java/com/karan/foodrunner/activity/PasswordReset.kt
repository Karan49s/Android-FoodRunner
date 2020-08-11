package com.karan.foodrunner.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.karan.foodrunner.R
import com.karan.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class PasswordReset : AppCompatActivity() {

    private lateinit var etOTP: EditText
    private lateinit var etPass: EditText
    private lateinit var etConfirmPassword3: EditText
    private lateinit var btnSubmit: Button
    private lateinit var rl: RelativeLayout
    private lateinit var mobileNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        etOTP = findViewById(R.id.etOTP)
        etPass = findViewById(R.id.etPass)
        etConfirmPassword3 = findViewById(R.id.etConfirmPassword)
        btnSubmit = findViewById(R.id.btnSubmit)
        rl = findViewById(R.id.rl)

        rl.visibility = View.VISIBLE
        if (intent != null) {
            mobileNumber = intent.getStringExtra("user_mobile") as String
        }
        btnSubmit.setOnClickListener {
            rl.visibility = View.GONE
            if (ConnectionManager().checkConnectivity(this@PasswordReset)) {
                if (etOTP.text.length == 4) {
                    if (etPass.length()>=4) {
                        if (etPass.text.toString()==etConfirmPassword3.text.toString())
                        {
                            resetPassword(
                                mobileNumber,
                                etOTP.text.toString(),
                                etPass.text.toString()
                            )
                        } else {
                            rl.visibility = View.VISIBLE
                            Toast.makeText(
                                this@PasswordReset,
                                "Passwords do not match",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        rl.visibility = View.VISIBLE
                        Toast.makeText(
                            this@PasswordReset,
                            "Invalid Password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    rl.visibility = View.VISIBLE
                    Toast.makeText(
                        this@PasswordReset,
                        "Incorrect OTP",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val dialog = AlertDialog.Builder(this@PasswordReset)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()

                }
                dialog.setNegativeButton("Exit") { _, _ ->

                    ActivityCompat.finishAffinity(this@PasswordReset)
                }
                dialog.create()
                dialog.show()
            }
        }
    }

    private fun resetPassword(mobileNumber: String, otp: String, password: String) {
        val queue = Volley.newRequestQueue(this@PasswordReset)
        val url = "http://13.235.250.119/v2/reset_password/fetch_result"

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("password", password)
        jsonParams.put("otp", otp)

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                try {
                    val obj = it.getJSONObject("data")
                    val success = obj.getBoolean("success")
                    if (success) {
                        val builder = AlertDialog.Builder(this@PasswordReset)
                        builder.setTitle("Confirmation")
                        builder.setMessage("Your Password has been successfully changed")
                        builder.setCancelable(false)
                        builder.setPositiveButton("Ok") { _, _ ->
                            startActivity(
                                Intent(
                                    this@PasswordReset,
                                    LoginActivity::class.java
                                )
                            )
                            ActivityCompat.finishAffinity(this@PasswordReset)
                        }
                        builder.create().show()
                    } else {
                        rl.visibility = View.VISIBLE
                        Toast.makeText(
                            this@PasswordReset,
                            "Some error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    rl.visibility = View.VISIBLE
                    Toast.makeText(
                        this@PasswordReset,
                        "Some exception occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
                rl.visibility = View.VISIBLE
                Toast.makeText(
                    this@PasswordReset,
                    "Volley error occurred!",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = getString(R.string.token)
                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }
}
