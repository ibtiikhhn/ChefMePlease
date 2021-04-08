package com.muibsols.chefmeplease.UI.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.muibsols.chefmeplease.Adapters.SideAdapter
import com.muibsols.chefmeplease.Listeners.SideClickListener
import com.muibsols.chefmeplease.Models.FinalOrder
import com.muibsols.chefmeplease.Models.Side
import com.muibsols.chefmeplease.Models.Topping
import com.muibsols.chefmeplease.R

class ChooseSideActivity : AppCompatActivity(),SideClickListener {

    lateinit var backBT:ImageView
    lateinit var sidesRV:RecyclerView
    lateinit var continueBT:Button
    lateinit var sideAdapter:SideAdapter
    val sides = ArrayList<Side>()
    val selectedSides = ArrayList<Side>()

    val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var dbRef: DatabaseReference
    val database = Firebase.database

    lateinit var receivedIntent: Intent
    var limit =0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_side)
        dbRef = database.reference
        receivedIntent = intent
        val finalOrder = receivedIntent.getSerializableExtra("ORDER") as FinalOrder
        limit = receivedIntent.getIntExtra("sideLimit",0)
        initViews()
        getData()

        backBT.setOnClickListener {
            finish()
        }
        continueBT.setOnClickListener {
            finalOrder.sides = selectedSides
            Log.i("HELL", "onCreate: "+selectedSides.size)
            val intent = Intent(this, OrderConfirmationActivity::class.java)
            intent.putExtra("ORDER", finalOrder)
//            intent.putExtra("sideLimit",optionModel.sides)
            startActivity(intent)
        }
    }

    fun initViews() {
        backBT = findViewById(R.id.sideBackBT)
        sidesRV = findViewById(R.id.sidesRV)
        continueBT = findViewById(R.id.sidesContinueBT)

        sidesRV.layoutManager = GridLayoutManager(baseContext, 2)
        sideAdapter = SideAdapter(this,this,limit)
        sidesRV.adapter = sideAdapter


    }

    fun getData() {
        dbRef?.child("Categories").child("Sides")?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sides.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val side = postSnapshot.getValue<Side>()
                    if (side != null) {
                        sides.add(side)
                    }
                }
                sideAdapter.sides = sides
                sideAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(baseContext, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onAddSide(side: Side) {
        selectedSides.add(side)
        Log.i("HELL", "onAddSide: "+selectedSides.size)
    }

    override fun onRemoveSide(side: Side) {
        selectedSides.remove(side)
        Log.i("HELL", "onAddSide: "+selectedSides.size)

    }

}