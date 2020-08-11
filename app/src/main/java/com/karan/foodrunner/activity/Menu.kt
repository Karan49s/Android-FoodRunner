package com.karan.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.karan.foodrunner.R
import com.karan.foodrunner.adapter.MenuRA
import com.karan.foodrunner.database.OrderEntity
import com.karan.foodrunner.database.RestaurantDatabase
import com.karan.foodrunner.model.MenuItem
import com.karan.foodrunner.util.ConnectionManager

class Menu : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    lateinit var imgFav: ImageButton
    private lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var coordinateLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var btnGoToCart: Button

    private val dishInfoList = arrayListOf<MenuItem>()
    private val orderList = arrayListOf<MenuItem>()

    lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerAdapter: MenuRA
    lateinit var progressLayout: RelativeLayout
    lateinit var restaurantName: String

    var restaurantId: String? = "000"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        sharedPreferences =getSharedPreferences(getString(R.string.prefrence_file_name),
            Context.MODE_PRIVATE)


        recyclerView = findViewById(R.id.recyclerView)

        //setHasOptionsMenu(true)

        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility= View.VISIBLE
        btnGoToCart = findViewById(R.id.btnGoToCart)
        coordinateLayout = findViewById(R.id.coordinateLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        imgFav = findViewById(R.id.imgFav)

        btnGoToCart.setOnClickListener {
            proceedToCart()
        }


        layoutManager = LinearLayoutManager(this@Menu)


        if (intent != null) {
            restaurantId = intent.getStringExtra("restaurant_id")
            restaurantName = intent.getStringExtra("restaurant_name")as String
        } else {
            finish()
            Toast.makeText(
                this@Menu,
                "Some unexpected Error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (restaurantId == "000") {
            finish()
            Toast.makeText(
                this@Menu,
                "Some unexpected Error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        setUpToolbar(restaurantName)

        val queue = Volley.newRequestQueue(this@Menu)

        if (ConnectionManager().checkConnectivity(this@Menu)) {
            val jsonRequest = object : JsonObjectRequest(Method.GET,
                "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId", null, Response.Listener {

                    try {
                        val obj2 = it.getJSONObject("data")
                        val success = obj2.getBoolean("success")
                        if (success) {
                            val data = obj2.getJSONArray("data")
                            progressLayout.visibility = View.GONE
                            for (i in 0 until data.length()) {
                                val dishJsonObject = data.getJSONObject(i)
                                val dishObject = MenuItem(

                                    dishJsonObject.getString("id"),
                                    dishJsonObject.getString("name"),
                                    dishJsonObject.getString("cost_for_one")
                                )

                                dishInfoList.add(dishObject)
                                recyclerAdapter = MenuRA(this@Menu, dishInfoList,
                                    object:MenuRA.OnItemClickListener{
                                        override fun onAddItemClick(dishObject: MenuItem) {
                                            orderList.add(dishObject)
                                            if (orderList.size > 0) {
                                                btnGoToCart.visibility = View.VISIBLE
                                                MenuRA.isCartEmpty = false
                                            }
                                        }

                                        override fun onRemoveItemClick(dishObject: MenuItem) {
                                            orderList.remove(dishObject)
                                            if (orderList.isEmpty()) {
                                                btnGoToCart.visibility = View.GONE
                                                MenuRA.isCartEmpty = true
                                            }
                                        }

                                    })

                                recyclerView.adapter = recyclerAdapter
                                recyclerView.itemAnimator = DefaultItemAnimator()
                                recyclerView.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                this@Menu,
                                "Some Error Occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@Menu, "Some Exception Occurred $e", Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this@Menu, "Volley error", Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = getString(R.string.token)
                    return headers
                }
            }
            queue.add(jsonRequest)
        } else {
            val dialog = AlertDialog.Builder(this@Menu)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()

            }
            dialog.setNegativeButton("Exit") { _, _ ->

                ActivityCompat.finishAffinity(this@Menu)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun proceedToCart(){
        val gson=Gson()
        val foodItems=gson.toJson(orderList)
        val async = CartItems(this@Menu, restaurantId.toString(), foodItems, 1).execute()
        val result = async.get()
        if (result) {

            val intent=Intent(this@Menu,Cart::class.java)
            intent.putExtra("resId", restaurantId )
            intent.putExtra("resName", restaurantName)
            startActivity(intent)

        } else {
            Toast.makeText(this@Menu, "Some unexpected error", Toast.LENGTH_SHORT).show()
        }

    }

    class CartItems(context: Context, private val restaurantId:String, private val foodItems:String, val mode:Int):
        AsyncTask<Void,Void,Boolean>(){
        val db=Room.databaseBuilder(context,RestaurantDatabase::class.java,"restaurants-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteAll()
            db.orderDao().insertOrder(OrderEntity(restaurantId, foodItems))
            db.close()
            return true

        }
    }




    private fun setUpToolbar(name:String){
        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title=name
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
