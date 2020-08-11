package com.karan.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.karan.foodrunner.R
import com.karan.foodrunner.adapter.HomeRA
import com.karan.foodrunner.model.RestaurantItem
import com.karan.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

lateinit var recyclerView: RecyclerView
lateinit var layoutManager: RecyclerView.LayoutManager

val restaurantInfoList = arrayListOf<RestaurantItem>()

lateinit var recyclerAdapter: HomeRA
lateinit var progressLayout: RelativeLayout
lateinit var progressBar: ProgressBar
lateinit var cont: Context

var ratingComparator= Comparator<RestaurantItem>{ restaurant1, restaurant2->
    if(restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true)==0)
    {
        restaurant1.restaurantName.compareTo(restaurant2.restaurantName,true)
    }
    else{
        restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true)
    }
}

class AllRestaurants : Fragment() {

    override fun onAttach(context: Context) {

        super.onAttach(context)
        cont=activity as Context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_all_restaurants, container, false )

        setHasOptionsMenu(true)

        recyclerView=view.findViewById(R.id.recyclerView)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)

        progressBar.visibility=View.VISIBLE
        layoutManager= LinearLayoutManager(activity)


        val queue= Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/"
        if(ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener<JSONObject> {
                    try {
                        progressLayout.visibility=View.GONE
                        val obj=it.getJSONObject("data")
                        val success = obj.getBoolean("success")
                        if (success) {
                            val data = obj.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantObject = RestaurantItem(

                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one")+" per person",
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantInfoList.add(restaurantObject)

                                recyclerAdapter = HomeRA(cont,restaurantInfoList)

                                recyclerView.adapter = recyclerAdapter
                                recyclerView.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }catch(e: JSONException){
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(activity as Context,"Volley error occurred", Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = getString(R.string.token)
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        }else
        {
            val dialog= AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Found")
            dialog.setPositiveButton("Open Settings"){ _, _ ->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()

            }
            dialog.setNegativeButton("Exit"){ _, _ ->

                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id= item.itemId
        if(id==R.id.action_sort){
            Collections.sort(restaurantInfoList,ratingComparator)
            restaurantInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }


}