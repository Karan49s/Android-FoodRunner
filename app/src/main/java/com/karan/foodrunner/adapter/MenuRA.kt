package com.karan.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.karan.foodrunner.R
import com.karan.foodrunner.model.MenuItem

class MenuRA(
    val context: Context, private val itemList: ArrayList<MenuItem>, private val listener: OnItemClickListener
) : RecyclerView.Adapter<MenuRA.DescriptionViewHolder>() {

    companion object {
        var isCartEmpty = true
    }


    class DescriptionViewHolder(view: View): RecyclerView.ViewHolder(view){

        val txtCount: TextView =view.findViewById(R.id.txtCount)
        val txtDishName: TextView =view.findViewById(R.id.txtDishName)
        val txtPrice: TextView =view.findViewById(R.id.txtPrice)
        val btnAdd: Button =view.findViewById(R.id.btnAdd)
        val btnRemove: Button =view.findViewById(R.id.btnRemove)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.rsr_menu,parent,false)
        return DescriptionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    interface OnItemClickListener {
        fun onAddItemClick(dishObject: MenuItem)
        fun onRemoveItemClick(dishObject: MenuItem)
    }



    override fun onBindViewHolder(holder: DescriptionViewHolder, position: Int) {
        val menuItem = itemList[position]
        holder.txtDishName.text = menuItem.dishName
        holder.txtPrice.text = menuItem.dishPrice
        holder.txtCount.text = (position + 1).toString()

        holder.btnAdd.setOnClickListener {
            holder.btnRemove.visibility = View.VISIBLE
            holder.btnAdd.visibility = View.GONE
            listener.onAddItemClick(menuItem)
        }

        holder.btnRemove.setOnClickListener {
            holder.btnRemove.visibility = View.GONE
            holder.btnAdd.visibility = View.VISIBLE
            listener.onRemoveItemClick(menuItem)
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

}
