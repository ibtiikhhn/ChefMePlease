package com.muibsols.chefmeplease.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muibsols.chefmeplease.Models.Chat
import com.muibsols.chefmeplease.R

class ChatAdapter(val context: Context?) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    val MSGLEFTTEXT =0
    val MSGRIGHTTEXT =1
    var chats : List<Chat> = ArrayList<Chat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == MSGLEFTTEXT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_item_left, parent, false)
            return ViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_item_right, parent, false)
            return ViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats[position]
        if (getItemViewType(position) == MSGLEFTTEXT) {
            holder.messageTV.setText(chat.message)
        }else{
            holder.messageTV.setText(chat.message)
        }

    }

    override fun getItemViewType(position: Int): Int {
        val chat = chats[position]
        if (chat.sender.equals("xjGpjfOc66UpXNkIzglVMuyzRAH3")) {
            return MSGLEFTTEXT
        }else{
            return MSGRIGHTTEXT
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTV: TextView = itemView.findViewById(R.id.chatText)
    }
}