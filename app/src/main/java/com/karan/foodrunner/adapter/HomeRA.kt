package com.karan.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.karan.foodrunner.R
import com.karan.foodrunner.activity.Menu
import com.karan.foodrunner.database.RestaurantDatabase
import com.karan.foodrunner.database.RestaurantEntity
import com.karan.foodrunner.model.RestaurantItem
import com.squareup.picasso.Picasso

class HomeRA(val context:Context, private val itemList:ArrayList<RestaurantItem>) :RecyclerView.Adapter<HomeRA.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtRestaurantName: TextView = view.findViewById(R.id.restsurantName)
        val txtPrice: TextView = view.findViewById(R.id.price)
        val txtRating: TextView = view.findViewById(R.id.rating)
        val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurant)
        val imgFav: ImageButton = view.findViewById(R.id.fab)

        val rlContent: RelativeLayout = view.findViewById(R.id.rl)

    }

    var button: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rsr_home, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.imgRestaurantImage.clipToOutline = true

        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRating.text = restaurant.restaurantRating
        holder.txtPrice.text = restaurant.restaurantCost_For_One
        // holder.imgRestaurantImage.setImageResource(restaurant.restaurantImage)

        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.logo)
            .into(holder.imgRestaurantImage)


        val listOfFav = GetFavAsyncTask(context).execute().get()

        if (listOfFav.isNotEmpty() && listOfFav.contains(restaurant.restaurantId)) {

            holder.imgFav.setBackgroundResource(R.drawable.ic_fav_true)
        } else {
            holder.imgFav.setBackgroundResource(R.drawable.ic_fav)
        }

        holder.imgFav.setOnClickListener {
            val restaurantEntity = RestaurantEntity(
                restaurant.restaurantId.toInt(),
                restaurant.restaurantName,
                restaurant.restaurantRating,
                restaurant.restaurantCost_For_One,
                restaurant.restaurantImage
            )
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                val data = async.get()
                if (data) {
                    holder.imgFav.setBackgroundResource(R.drawable.ic_fav_true)
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val data = async.get()

                if (data) {
                    holder.imgFav.setBackgroundResource(R.drawable.ic_fav)
                }
            }
        }

        holder.rlContent.setOnClickListener{
            val intent=Intent(context, Menu::class.java)
            intent.putExtra("restaurant_id",restaurant.restaurantId)
            intent.putExtra("restaurant_name",restaurant.restaurantName)
            context.startActivity(intent)
        }
    }
}

class GetFavAsyncTask(context: Context) : AsyncTask<Void, Void, List<String>>() {
    private val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db")
        .build()

    override fun doInBackground(vararg params: Void?): List<String> {
        val list = db.restaurantDao().getAllRestaurants()
        val listOfIds = arrayListOf<String>()
        for (i in list) {
            listOfIds.add(i.id.toString())
        }
        return listOfIds
    }
}

class DBAsyncTask(context: Context, private val restaurantEntity: RestaurantEntity, private val mode: Int) :
    AsyncTask<Void, Void, Boolean>() {

    private val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db")
        .build()

    override fun doInBackground(vararg params: Void?): Boolean {
        when (mode) {

            1 -> {
                val res: RestaurantEntity? =
                    db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                db.close()
                return res != null
            }

            2 -> {
                db.restaurantDao().insertRestaurant(restaurantEntity)
                db.close()
                return true
            }

            3 -> {
                db.restaurantDao().deleteRestaurant(restaurantEntity)
                db.close()
                return true
            }
        }
        return true
    }
}




