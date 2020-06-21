package com.karan.foodrunner.activity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.karan.foodrunner.R

class LoggedInActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)


        sharedPreferences =getSharedPreferences(getString(R.string.prefrence_file_name),
            Context.MODE_PRIVATE)

        val mobile= sharedPreferences.getString("mobile","0000000000")
    }
}