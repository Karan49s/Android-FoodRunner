package com.karan.foodrunner.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
import com.karan.foodrunner.adapter.CartRA
import com.karan.foodrunner.adapter.MenuRA
import com.karan.foodrunner.database.OrderEntity
import com.karan.foodrunner.database.RestaurantDatabase
import com.karan.foodrunner.model.MenuItem
import org.json.JSONArray
import org.json.JSONObject

class Cart : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var coordinateLayout: CoordinatorLayout
    private lateinit var toolbar: Toolbar
    private val orderList=ArrayList<MenuItem>()
    lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    lateinit var rlMyCart: RelativeLayout
    private lateinit var txtResName: TextView
    private lateinit var recyclerAdapter: CartRA
    private lateinit var frameLayout: FrameLayout
    private lateinit var btnOrder: Button
    private  var resId:String?="0"
    private  var resName:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this@Cart)
        coordinateLayout = findViewById(R.id.coordinateLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        txtResName = findViewById(R.id.txtResName)

        progressBar = findViewById(R.id.progressBar)
        rlMyCart = findViewById(R.id.rlMyCart)
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.GONE
        btnOrder=findViewById(R.id.btnOrder)
        btnOrder.visibility = View.VISIBLE

        setUpToolbar()

        resId = intent.getStringExtra("resId")as String
        resName = intent.getStringExtra("resName") as String
        txtResName.text = resName



        val list=GetItemsDBAsync(applicationContext).execute().get()
        for(element in list){

            orderList.addAll(Gson().fromJson(element.foodItems,Array<MenuItem>::class.java).asList())
        }
        if(orderList.isEmpty())
        {
            rlMyCart.visibility= View.GONE
            progressLayout.visibility= View.VISIBLE
        }
        else{
            rlMyCart.visibility= View.VISIBLE
            progressLayout.visibility= View.GONE
        }

        recyclerAdapter= CartRA(orderList,this@Cart)
        layoutManager = LinearLayoutManager(this@Cart)
        recyclerView.layoutManager=layoutManager
        recyclerView.itemAnimator= DefaultItemAnimator()
        recyclerView.adapter=recyclerAdapter

        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].dishPrice.toInt()
        }
        val total = "Place Order(Total: Rs. $sum)"
        btnOrder.text = total
        btnOrder.visibility = View.VISIBLE
        btnOrder.setOnClickListener {
            progressLayout.visibility = View.VISIBLE
            rlMyCart.visibility = View.INVISIBLE
            sendRequest()
        }
    }


    class GetItemsDBAsync(context: Context): AsyncTask<Void, Void, List<OrderEntity>>()
    {
        private val db= Room.databaseBuilder( context, RestaurantDatabase::class.java,"restaurants-db").build()
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            return db.orderDao().getAllOrders()
        }
    }


    private  fun setUpToolbar() {

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    private  fun sendRequest() {

        val queue = Volley.newRequestQueue(this@Cart)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"

        val jsonParams = JSONObject()
        jsonParams.put("user_id", this@Cart.getSharedPreferences(getString(R.string.prefrence_file_name), Context.MODE_PRIVATE).getString(
            "user_id", null
        ) as String)

        jsonParams.put("restaurant_id", resId as String)
        var total= 0
        for (i in 0 until orderList.size) {
            total+= orderList[i].dishPrice.toInt()
        }
        jsonParams.put("total_cost", total.toString())
        val dishArray = JSONArray()
        for (i in 0 until orderList.size) {
            val dishId = JSONObject()
            dishId.put("food_item_id", orderList[i].dishId)
            dishArray.put(i, dishId)
        }
        jsonParams.put("food", dishArray)
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
            try {
                val obj = it.getJSONObject("data")
                val success = obj.getBoolean("success")
                if (success) {
                    ClearDBAsync(applicationContext, resId.toString()).execute().get()
                    MenuRA.isCartEmpty = true
                    val dialog = Dialog(
                        this@Cart, android.R.style.Theme_Black_NoTitleBar_Fullscreen
                    )
                    dialog.setContentView(R.layout.activity_order_placed)
                    dialog.show()
                    dialog.setCancelable(false)
                    val btnOk = dialog.findViewById<Button>(R.id.btnOk)
                    btnOk.setOnClickListener {
                        dialog.dismiss()
                        startActivity(Intent(this@Cart, LoggedInActivity::class.java))
                        ActivityCompat.finishAffinity(this@Cart)
                    }
                } else {
                    rlMyCart.visibility = View.VISIBLE
                    Toast.makeText(this@Cart, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                rlMyCart.visibility = View.VISIBLE
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            rlMyCart.visibility = View.VISIBLE
            Toast.makeText(this@Cart, "Volley Error Occurred", Toast.LENGTH_SHORT).show()
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

    class ClearDBAsync(context: Context, private val resId:String): AsyncTask<Void, Void, Boolean>(){
        private val db= Room.databaseBuilder(context,RestaurantDatabase::class.java,"restaurants-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        ClearDBAsync(applicationContext,resId.toString()).execute().get()
        MenuRA.isCartEmpty=true
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        startActivity(
            Intent(
                this@Cart,
                LoggedInActivity::class.java
            )
        )

    }
}
