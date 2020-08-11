package com.karan.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.karan.foodrunner.R
import com.karan.foodrunner.model.MenuItem

class CartRA(private val cartArray:ArrayList<MenuItem>, val context: Context): RecyclerView.Adapter<CartRA.CartViewHolder>(){

    class CartViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtDishName: TextView =view.findViewById(R.id.txtDishName)
        val txtCost: TextView =view.findViewById(R.id.txtCost)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.rsr_cart,parent,false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartArray.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartObj=cartArray[position]
        holder.txtDishName.text=cartObj.dishName
        val price="Rs. ${cartObj.dishPrice}"
        holder.txtCost.text=price
    }

}