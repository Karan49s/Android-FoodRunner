package com.karan.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import com.karan.foodrunner.R

class SplashActivity : AppCompatActivity() {
    lateinit var logo :ImageView
    lateinit var appName : TextView

    private val SPLASH_TIME_OUT:Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        logo = findViewById(R.id.imgLogo)
        appName = findViewById(R.id.appName)


        Handler().postDelayed({
            startActivity(Intent(this,
                LoginActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)



    }
}
