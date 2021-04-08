package com.muibsols.chefmeplease.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.muibsols.chefmeplease.Listeners.SpiceClickListener
import com.muibsols.chefmeplease.Models.Spice
import com.muibsols.chefmeplease.Models.Topping
import com.muibsols.chefmeplease.R

class SpiceAdapter(val context: Context?, val spiceClickListener: SpiceClickListener,val limit:Int) :
    RecyclerView.Adapter<SpiceAdapter.ViewHolder>() {
    var spices: List<Spice> = ArrayList<Spice>()
    val TAG = "ADAPTER"
    var selected = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mini_tv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spice = spices[position]
        holder.name.text = spice.name+" $"+spice.price

        holder.itemView.setOnClickListener {
            if (spice.selected) {
                Log.i(TAG, "onBindViewHolder: if py aya")
                spice.selected = false
                holder.name.setTextColor(ContextCompat.getColor(context!!, R.color.accent_secondary))
                holder.name.background =
                    (ContextCompat.getDrawable(context!!, R.drawable.round_corner_tv))
                holder.name.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(context, R.drawable.ic_baseline_add_24),
                    null
                )
                spiceClickListener.onClickRemoveSpice(spice)
                selected--


            } else {
                if (limit == selected) {
                    Toast.makeText(context,"You Can Only Select $limit ",Toast.LENGTH_LONG).show()
                }else {
                    spice.selected = true
                    holder.name.background =
                            (ContextCompat.getDrawable(context!!, R.drawable.round_corner_tv_select))
                    holder.name.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                    holder.name.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            ContextCompat.getDrawable(context, R.drawable.ic_baseline_done_24),
                            null
                    )
                    spiceClickListener.onClickAddSpice(spice)
                    selected++
                }
            }
//            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return spices.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nameTV)
    }

}