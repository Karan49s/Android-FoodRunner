package com.karan.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.karan.foodrunner.R
import com.karan.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class ForgotPassword : AppCompatActivity() {
    private lateinit var etMobileNumber3: EditText
    private lateinit var etMailAddress: EditText
    private lateinit var btnNext: Button
    private lateinit var rlContent: RelativeLayout


    private var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private var mobilePattern = "[0-9]{10}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etMobileNumber3=findViewById(R.id.etMobileNumber)
        etMailAddress=findViewById(R.id.etEmail)
        btnNext=findViewById(R.id.btnNext)
        rlContent=findViewById(R.id.rl)
        rlContent.visibility= View.VISIBLE

        btnNext.setOnClickListener {

            if (validations(etMobileNumber3.text.toString(), etMailAddress.text.toString())) {
                if (ConnectionManager().checkConnectivity(this@ForgotPassword)) {

                    sendOTP(etMobileNumber3.text.toString(), etMailAddress.text.toString())
                } else {
                    Toast.makeText(
                        this@ForgotPassword,
                        "No Internet Connection!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun sendOTP(mobileNumber:String,email:String) {
        val queue = Volley.newRequestQueue(this@ForgotPassword)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("email", email)

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val firstTry = data.getBoolean("first_try")
                        if (firstTry) {
                            val builder = AlertDialog.Builder(this@ForgotPassword)
                            builder.setTitle("Information")
                            builder.setMessage("Please check your registered Email for the OTP")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Ok") { _, _ ->
                                val intent = Intent(
                                    this@ForgotPassword,
                                    PasswordReset::class.java
                                )
                                intent.putExtra("user_mobile", mobileNumber)
                                startActivity(intent)
                            }

                            builder.create().show()
                        } else {
                            val builder = AlertDialog.Builder(this@ForgotPassword ,R.style.AlertDialog)
                            builder.setTitle("Information")
                            builder.setMessage("Please refer to the previous email for the OTP.")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Ok") { _, _ ->
                                val intent = Intent(
                                    this@ForgotPassword,
                                    PasswordReset::class.java
                                )
                                intent.putExtra("user_mobile", mobileNumber)
                                startActivity(intent)
                            }
                            builder.create().show()
                        }
                    } else {
                        Toast.makeText(
                            this@ForgotPassword,
                            "Mobile number not registered!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@ForgotPassword,
                        "Incorrect response error!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(this@ForgotPassword, "Volley Error!", Toast.LENGTH_SHORT).show()
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

    private fun validations(phone:String,email:String):Boolean {

        if (phone.isEmpty()) {
            Toast.makeText(this@ForgotPassword, "Enter Mobile Number", Toast.LENGTH_LONG).show()
            return false
        } else {
            if (email.isEmpty()) {
                Toast.makeText(this@ForgotPassword, "Enter Password", Toast.LENGTH_LONG).show()
                return false
            } else {
                if (!phone.trim().matches(mobilePattern.toRegex())) {
                    Toast.makeText(
                        this@ForgotPassword,
                        "Enter a valid Mobile number",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    return false
                } else {
                    return if (!email.trim().matches(emailPattern.toRegex())) {
                        Toast.makeText(
                            this@ForgotPassword,
                            "Enter a valid Email Id",
                            Toast.LENGTH_LONG
                        ).show()
                        false
                    } else
                        true
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
