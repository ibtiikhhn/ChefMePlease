package com.muibsols.chefmeplease.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.muibsols.chefmeplease.Listeners.BeverageClickListener
import com.muibsols.chefmeplease.Models.BeverageModel
import com.muibsols.chefmeplease.R

class BeverageAdapter (val context: Context?, val beverageClickListener: BeverageClickListener) : RecyclerView.Adapter<BeverageAdapter.ViewHolder>() {
    var beverages: List<BeverageModel> = ArrayList<BeverageModel>()
    var row_index = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cook_tv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.setText(beverages[position].name)
        holder.itemView.setOnClickListener {
            row_index = position
            notifyDataSetChanged()
            beverageClickListener.onBeverageClick(beverages[position])
        }
        if (row_index == position) {
            holder.name.background = (ContextCompat.getDrawable(context!!, R.drawable.corner_full_selected))
            holder.name.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        } else {
            holder.name.background = (ContextCompat.getDrawable(context!!, R.drawable.corner_full))
            holder.name.setTextColor(ContextCompat.getColor(context!!, R.color.accent_secondary))
        }
    }

    override fun getItemCount(): Int {
        return beverages.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.cookTV)
    }
}