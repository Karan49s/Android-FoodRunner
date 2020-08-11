package com.karan.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.karan.foodrunner.R
import com.karan.foodrunner.fragment.*

class LoggedInActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinateLayout: CoordinatorLayout
    private lateinit var toolbar: Toolbar
    private lateinit var frameLayout: FrameLayout
    private lateinit var navigationView: NavigationView

    private var previousMenuItem: MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)

        title="All Restaurants"

        drawerLayout=findViewById(R.id.drawerLayout)
        coordinateLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frame)
        navigationView=findViewById(R.id.navigationView)
        sharedPreferences =getSharedPreferences(getString(R.string.prefrence_file_name),
            Context.MODE_PRIVATE)

        setUpToolbar()
        openHome()

        val view=
            LayoutInflater.from(this@LoggedInActivity).inflate(R.layout.drawer_header,null)
        val userName: TextView =view.findViewById(R.id.username)
        val userPhone: TextView =view.findViewById(R.id.usernumber)
        userName.text=sharedPreferences.getString("user_name",null)
        val phone="+91-${sharedPreferences.getString("user_mobile_number",null)}"
        userPhone.text=phone
        navigationView.addHeaderView(view)

        val actionBarDrawerToggle= ActionBarDrawerToggle(this@LoggedInActivity,drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        AllRestaurants()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "All Restaurants"
                }

                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        Profile()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "My Profile"
                }

                R.id.favourites -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        FavouriteFragment()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Favourite Restaurants"
                }

                R.id.history -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        OrderHistory()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "My Previous Orders"
                }

                R.id.faqs -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        Faqs()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Frequently Asked Questions"
                }

                R.id.logout -> {
                    val builder = AlertDialog.Builder(this@LoggedInActivity,R.style.AlertDialog)
                    builder.setTitle("Log Out!!!")
                    builder.setMessage("Are you sure you want to log out?")
                    builder.setPositiveButton("Yes") { _, _ ->
                        val intent = Intent(this@LoggedInActivity, LoginActivity::class.java)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.clear()
                        editor.apply()
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.dismiss()
                        startActivity(intent)
                        finish()
                    }
                    builder.setNegativeButton("No") { _, _ -> }
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                }
            }

            return@setNavigationItemSelectedListener (true)
        }
    }
    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openHome(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, AllRestaurants()).commit()
        supportActionBar?.title="All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
        when(supportFragmentManager.findFragmentById(R.id.frame)){

            !is AllRestaurants -> openHome()
            else->super.onBackPressed()

        }


    }
}
