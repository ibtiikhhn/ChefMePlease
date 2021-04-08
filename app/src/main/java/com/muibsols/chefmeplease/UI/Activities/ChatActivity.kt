package com.muibsols.chefmeplease.UI.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.muibsols.chefmeplease.Adapters.ChatAdapter
import com.muibsols.chefmeplease.Models.Chat
import com.muibsols.chefmeplease.Models.ChatList
import com.muibsols.chefmeplease.Models.User
import com.muibsols.chefmeplease.R

class ChatActivity : AppCompatActivity() {

    lateinit var chatBackBT:ImageView
    lateinit var sendMessageBT:ImageView
    lateinit var messageET:EditText
    lateinit var chatRV:RecyclerView
    lateinit var adapter: ChatAdapter
    val chats = ArrayList<Chat>()

    val database = Firebase.database
    val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initViews()
        dbRef = database.reference
        readChats()

        sendMessageBT.setOnClickListener {
            if (messageET.text.isEmpty()) {
                Toast.makeText(baseContext, "Empty Message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val chat =Chat(firebaseAuth.currentUser?.uid?:"","xjGpjfOc66UpXNkIzglVMuyzRAH3",messageET.text.toString())
            dbRef.child("Chats").child(firebaseAuth.currentUser?.uid ?: "").push().setValue(chat)
            messageET.setText("")

            val userId = firebaseAuth.currentUser?.uid
            dbRef.child("Users").child(userId?:"null").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue<User>()
                    dbRef.child("ChatList").child(firebaseAuth.currentUser?.uid ?: "").setValue(user)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }

        chatBackBT.setOnClickListener {
            finish()
        }


    }

    fun readChats() {
        dbRef.child("Chats").child(firebaseAuth.currentUser?.uid?:"").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chats.clear()
                for (postSnapshot in snapshot.children) {
                    val chat = postSnapshot.getValue<Chat>()
                    if (chat != null) {
                        chats.add(chat)
                    }
                }
                adapter.chats = chats
                adapter.notifyDataSetChanged()            }

            override fun onCancelled(error: DatabaseError) {
 /*               TODO("Not yet implemented")*/
            }

        })
    }

    fun initViews() {
        chatBackBT = findViewById(R.id.chatBackBT)
        sendMessageBT = findViewById(R.id.sendMessageBT)
        messageET = findViewById(R.id.messageET)
        chatRV = findViewById(R.id.chatRV)
        chatRV.layoutManager = LinearLayoutManager(baseContext)

        adapter = ChatAdapter(baseContext)
        chatRV.adapter = adapter

    }

}