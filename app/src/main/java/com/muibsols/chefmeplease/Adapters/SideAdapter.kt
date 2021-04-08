package com.muibsols.chefmeplease.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.muibsols.chefmeplease.Listeners.SideClickListener
import com.muibsols.chefmeplease.Models.Side
import com.muibsols.chefmeplease.R

class SideAdapter (val context: Context?, val sideClickListener: SideClickListener, val limit:Int) : RecyclerView.Adapter<SideAdapter.ViewHolder>() {
    var sides : List<Side> = ArrayList<Side>()
    var selected = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.mini_tv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val side = sides[position]
        holder.name.text = side.name+" $"+side.price

        holder.itemView.setOnClickListener {
            if (side.selected) {
                side.selected = false
                holder.name.setTextColor(ContextCompat.getColor(context!!, R.color.accent_secondary))
                holder.name.background= (ContextCompat.getDrawable(context!!, R.drawable.round_corner_tv))
                holder.name.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_baseline_add_24), null)
                sideClickListener.onRemoveSide(side)
                selected--

            } else {
                if (limit == selected) {
                    Toast.makeText(context,"You Can Only Select $limit", Toast.LENGTH_LONG).show()
                }else{
                    side.selected=true
                    holder.name.background= (ContextCompat.getDrawable(context!!, R.drawable.round_corner_tv_select))
                    holder.name.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                    holder.name.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_baseline_done_24), null)
                    sideClickListener.onAddSide(side)
                    selected++
                }

            }
        }

    }

    override fun getItemCount(): Int {
        return sides.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nameTV)
    }
}