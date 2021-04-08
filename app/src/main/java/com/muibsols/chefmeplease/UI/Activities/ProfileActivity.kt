package com.muibsols.chefmeplease.UI.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
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
import com.muibsols.chefmeplease.Adapters.OrderAdapter
import com.muibsols.chefmeplease.Models.FinalOrder
import com.muibsols.chefmeplease.Models.Order
import com.muibsols.chefmeplease.Models.User
import com.muibsols.chefmeplease.R
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    val TAG = "PROFILE"

    lateinit var userNameTV:TextView
    lateinit var userEmailTV:TextView
    lateinit var userAddressTV:TextView
    lateinit var editProfileBT:ImageView
    lateinit var userImg:CircleImageView
    lateinit var userOrdersRV:RecyclerView
    lateinit var adapter :OrderAdapter
    lateinit var bactBT:ImageView
    val orderList = ArrayList<FinalOrder>()


    val database = Firebase.database
    val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews()
        dbRef = database.reference

        val userId = firebaseAuth.currentUser?.uid
        dbRef.child("Users").child(userId?:"null").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>()
                userNameTV.text = user?.name
                userEmailTV.text = user?.email
                Glide.with(baseContext).load(user?.imageUrl).into(userImg)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        dbRef.child("UserOrders").child(userId?:"null").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val order = postSnapshot.getValue<FinalOrder>()
                    Log.i(TAG, "onDataChange: "+order.toString())
                    if (order != null) {
                        orderList.add(order)
                    }
                }
                adapter.orders = orderList
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "onCancelled: " + error)
            }

        })

        bactBT.setOnClickListener {
            finish()
        }

    }

    fun initViews() {
        userNameTV = findViewById(R.id.userNameTV)
        userEmailTV = findViewById(R.id.userEmailTV)
        userImg = findViewById(R.id.userProfileIV)
        userOrdersRV = findViewById(R.id.activeOrdersRV)
        bactBT = findViewById(R.id.PfBackBT)

        userOrdersRV.layoutManager= LinearLayoutManager(this)
        adapter = OrderAdapter(baseContext)
        userOrdersRV.adapter = adapter
    }
}