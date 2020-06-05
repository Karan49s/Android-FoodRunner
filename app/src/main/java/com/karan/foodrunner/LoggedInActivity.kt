package com.karan.foodrunner

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LoggedInActivity : AppCompatActivity() {
    lateinit var greet: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)

        greet= findViewById(R.id.greeting)

        sharedPreferences =getSharedPreferences(getString(R.string.prefrence_file_name),
            Context.MODE_PRIVATE)

        val mobile= sharedPreferences.getString("mobile","0000000000")


        greet.append(mobile)
    }
}