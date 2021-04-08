package com.muibsols.chefmeplease.UI.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.muibsols.chefmeplease.Adapters.OrderAdapter
import com.muibsols.chefmeplease.Models.FinalOrder
import com.muibsols.chefmeplease.Models.Order
import com.muibsols.chefmeplease.R

class ActiveOrdersActivity : AppCompatActivity() {

    lateinit var bactBT: ImageView
    val orderList = ArrayList<FinalOrder>()
    lateinit var userOrdersRV: RecyclerView
    lateinit var adapter: OrderAdapter
    val database = Firebase.database
    val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_orders)
        initViews()
        val userId = firebaseAuth.currentUser?.uid
        dbRef = database.reference
        dbRef.child("UserOrders").child(userId?:"").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                orderList.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val order = postSnapshot.getValue<FinalOrder>()
                    if (order != null) {
                        if (order.orderStatus.equals("Pending")) {
                            orderList.add(order)
                        }
                    }
                }
                adapter.orders = orderList
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        bactBT.setOnClickListener {
            finish()
        }

    }

    fun initViews() {
        userOrdersRV = findViewById(R.id.activeOrdersRV)
        bactBT = findViewById(R.id.AoBackBT)

        userOrdersRV.layoutManager = LinearLayoutManager(this)
        adapter = OrderAdapter(baseContext)
        userOrdersRV.adapter = adapter

    }

}