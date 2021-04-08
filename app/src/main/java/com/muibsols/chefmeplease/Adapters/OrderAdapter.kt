package com.muibsols.chefmeplease.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muibsols.chefmeplease.Listeners.DishClickListener
import com.muibsols.chefmeplease.Models.Dish
import com.muibsols.chefmeplease.Models.FinalOrder
import com.muibsols.chefmeplease.Models.Order
import com.muibsols.chefmeplease.R

class OrderAdapter(val context: Context?) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    var orders: List<FinalOrder> = ArrayList<FinalOrder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_cv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        if (context != null) {
            Glide.with(context).load(order.mainOrder?.get(0)?.dish?.image).into(holder.image)
        }
        holder.name.setText(order.mainOrder?.get(0)?.dish?.name)
        if (order.mainOrder?.size !!> 1) {
            holder.name.setText(order.mainOrder?.get(0)?.dish?.name+", "+order.mainOrder?.get(1)?.dish?.name)
        }
        holder.price.setText(order.price+" $")
        holder.date.setText(order.date)
        holder.status.setText(order.orderStatus)
        holder.time.setText(order.time)

    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.orderDishIMG)
        val name: TextView = itemView.findViewById(R.id.orderDishNameTV)
        val price: TextView = itemView.findViewById(R.id.orderDishPriceTV)
        val date: TextView = itemView.findViewById(R.id.orderDateTV)
        val time: TextView = itemView.findViewById(R.id.orderTImeTV)
        val status: TextView = itemView.findViewById(R.id.orderStatusTV)
    }
}