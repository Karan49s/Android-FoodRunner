package com.karan.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.karan.foodrunner.R
import com.karan.foodrunner.adapter.HomeRA
import com.karan.foodrunner.database.RestaurantDatabase
import com.karan.foodrunner.database.RestaurantEntity
import com.karan.foodrunner.model.RestaurantItem


class FavouriteFragment : Fragment() {
    private lateinit var recyclerFav: RecyclerView
    lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var rlFav: RelativeLayout
    private lateinit var rlNoFav: RelativeLayout
    private lateinit var recyclerAdapter: HomeRA

    private var dbResList= arrayListOf<RestaurantItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view=inflater.inflate(R.layout.fragment_favourite, container, false)

        recyclerFav=view.findViewById(R.id.recyclerFav)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        rlFav=view.findViewById(R.id.rlFav)
        rlNoFav=view.findViewById(R.id.rlNoFav)

        progressLayout.visibility=View.VISIBLE
        setUpRecycler(view)

        return view
    }
    private fun setUpRecycler(view: View)
    {
        recyclerFav=view.findViewById(R.id.recyclerFav)
        val resList=FavouriteAsync(activity as Context).execute().get()
        if(resList.isEmpty()){
            progressLayout.visibility=View.GONE
            rlFav.visibility=View.GONE
            rlNoFav.visibility=View.VISIBLE
        }else{
            rlFav.visibility=View.VISIBLE
            rlNoFav.visibility=View.GONE
            progressLayout.visibility=View.GONE

            for (i in resList){
                dbResList.add(
                    RestaurantItem(
                        i.id.toString(),
                        i.name,
                        i.rating,
                        i.costForTwo,
                        i.imageUrl
                    )
                )
            }
            recyclerAdapter= HomeRA(activity as Context,dbResList)
            val layoutManager= LinearLayoutManager(activity)
            recyclerFav.layoutManager=layoutManager
            recyclerFav.itemAnimator= DefaultItemAnimator()
            recyclerFav.adapter=recyclerAdapter
            recyclerFav.setHasFixedSize(true)

        }
    }
    class FavouriteAsync( context: Context): AsyncTask<Void, Void, List<RestaurantEntity>>() {
        private val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurants-db").build()

        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            return db.restaurantDao().getAllRestaurants()
        }
    }
}
