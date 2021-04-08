package com.muibsols.chefmeplease.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muibsols.chefmeplease.Listeners.DishClickListener
import com.muibsols.chefmeplease.Listeners.FavClickListener
import com.muibsols.chefmeplease.Models.Dish
import com.muibsols.chefmeplease.R

class FavAdapter (val context: Context?, val dishClickListener: DishClickListener, val favClickListener: FavClickListener) : RecyclerView.Adapter<FavAdapter.ViewHolder>() {

    var dishes : List<Dish> = ArrayList<Dish>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.dish_cv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (context != null) {
            Glide.with(context).load(dishes[position].image).into(holder.image)
        }
        holder.name.setText(dishes[position].name)
        holder.price.setText(dishes[position].price)
        holder.itemView.setOnClickListener {
            dishClickListener.onDishClick(dishes[position])
        }
        holder.favIMG.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_favorite_24))
        holder.favIMG.setOnClickListener {
            favClickListener.onFavClick(dishes[position])
        }
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.dish_img)
        val name: TextView = itemView.findViewById(R.id.name_tv)
        val price: TextView = itemView.findViewById(R.id.price_tv)
        val favIMG: ImageView = itemView.findViewById(R.id.favIMG)
    }
}