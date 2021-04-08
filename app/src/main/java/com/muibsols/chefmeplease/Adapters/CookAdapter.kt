package com.muibsols.chefmeplease.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.muibsols.chefmeplease.Listeners.CookStyleClickListener
import com.muibsols.chefmeplease.Models.CookStyle
import com.muibsols.chefmeplease.R

class CookAdapter (val context: Context?,val cookStyleClickListener: CookStyleClickListener) : RecyclerView.Adapter<CookAdapter.ViewHolder>() {
    var cookStyles: List<CookStyle> = ArrayList<CookStyle>()
    var row_index = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cook_tv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.setText(cookStyles[position].name)
        holder.itemView.setOnClickListener {
            row_index = position
            notifyDataSetChanged()
            cookStyleClickListener.onCookStyleClick(cookStyles[position])
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
        return cookStyles.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.cookTV)
    }
}