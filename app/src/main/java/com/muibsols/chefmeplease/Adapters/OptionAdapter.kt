package com.muibsols.chefmeplease.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.muibsols.chefmeplease.Listeners.BeverageClickListener
import com.muibsols.chefmeplease.Listeners.OptionClickListener
import com.muibsols.chefmeplease.Models.OptionModel
import com.muibsols.chefmeplease.R

class OptionAdapter (val context: Context?, val optionClickListener: OptionClickListener) : RecyclerView.Adapter<OptionAdapter.ViewHolder>() {
    var options: List<OptionModel> = ArrayList<OptionModel>()
    var row_index = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.options_cv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val optionModel = options[position]
        holder.name.setText(optionModel.name)
        holder.dishes.text = "Select ${optionModel.totalDishes} Dish ${optionModel.sides} Sides"
        if (optionModel.saladIncluded) {
            holder.saladInclude.text="(Salad Included)"
        }else{
            holder.saladInclude.text="(Salad Unavailable)"
        }
        holder.itemView.setOnClickListener {
            row_index = position
            notifyDataSetChanged()
            optionClickListener.onOptionClick(optionModel)
        }
        if (row_index == position) {
            holder.img.setImageDrawable((ContextCompat.getDrawable(context!!, R.drawable.ic_baseline_check_circle_24)))
            holder.name.setTextColor(ContextCompat.getColor(context!!, R.color.accent_secondary))
/*            holder.name.background =
                (ContextCompat.getDrawable(context!!, R.drawable.corner_full_selected))
            holder.name.setTextColor(ContextCompat.getColor(context!!, R.color.white))*/
        } else {
            holder.img.setImageDrawable((ContextCompat.getDrawable(context!!, R.drawable.ic_baseline_check_circle_outline_24)))
            holder.name.setTextColor(ContextCompat.getColor(context!!, R.color.black))
/*            holder.name.background = (ContextCompat.getDrawable(context!!, R.drawable.corner_full))
            holder.name.setTextColor(ContextCompat.getColor(context!!, R.color.accent_secondary))*/
        }
    }

    override fun getItemCount(): Int {
        return options.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.optionNameTV)
        val dishes :TextView = itemView.findViewById(R.id.optionDishesTV)
        val saladInclude:TextView = itemView.findViewById(R.id.saladTV)
        val img : ImageView = itemView.findViewById(R.id.checkIMG)
    }
}