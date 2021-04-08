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

class DishAdapter(val context: Context?,val dishClickListener: DishClickListener,val favClickListener: FavClickListener) : RecyclerView.Adapter<DishAdapter.ViewHolder>() {

    var dishes : List<Dish> = ArrayList<Dish>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.dish_cv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]

        if (context != null) {
            Glide.with(context).load(dish.image).into(holder.image)
        }
        holder.name.setText(dish.name)
        holder.price.setText("$ " +dish.price)
        holder.itemView.setOnClickListener {
            dishClickListener.onDishClick(dish)
        }
        holder.favIMG.setOnClickListener {
            favClickListener.onFavClick(dish)
        }
/*        val drawble = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            context?.resources?.getDrawable(R.drawable.dish_placeholder,context.theme)
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }*/
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.dish_img)
        val name: TextView = itemView.findViewById(R.id.name_tv)
        val price: TextView = itemView.findViewById(R.id.price_tv)
        val favIMG:ImageView = itemView.findViewById(R.id.favIMG)
    }
}
