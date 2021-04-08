package com.muibsols.chefmeplease.UI.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.muibsols.chefmeplease.Adapters.DishAdapter
import com.muibsols.chefmeplease.Adapters.OptionAdapter
import com.muibsols.chefmeplease.Listeners.DishClickListener
import com.muibsols.chefmeplease.Listeners.FavClickListener
import com.muibsols.chefmeplease.Models.Dish
import com.muibsols.chefmeplease.Models.FinalOrder
import com.muibsols.chefmeplease.Models.OptionModel
import com.muibsols.chefmeplease.R

class ChooseAnotherDishActivity : AppCompatActivity(),DishClickListener,FavClickListener {
    lateinit var chickenRV: RecyclerView
    lateinit var adapter : DishAdapter
    lateinit var optionsRV: RecyclerView
    lateinit var bacBT:ImageView
    lateinit var receivedIntent: Intent
    lateinit var optionModel:OptionModel
    lateinit var finalOrder: FinalOrder

    val dishes = ArrayList<Dish>()


    val database = Firebase.database
    val myDbRef = database.reference

    val dbRef = database.getReference("Categories")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_another_dish)
        receivedIntent = intent

        finalOrder = receivedIntent.getSerializableExtra("ORDER") as FinalOrder


        val category = receivedIntent.getStringExtra("category")
        Toast.makeText(baseContext,category,Toast.LENGTH_SHORT).show()
        optionModel = receivedIntent.getSerializableExtra("option") as OptionModel
        initViews()
        getDishes()

        bacBT.setOnClickListener {
            finish()
        }

    }

    fun initViews() {
        bacBT = findViewById(R.id.sdBackBT)
        chickenRV = findViewById(R.id.sdDishRV)
        chickenRV.layoutManager= GridLayoutManager(baseContext,2)
        adapter = DishAdapter(baseContext,this,this)
        chickenRV.adapter = adapter
    }

    fun getDishes() {

        dbRef.child("Steak").child("Dishes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dishes.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val dish = postSnapshot.getValue<Dish>()
                    if (dish != null) {
                        dishes.add(dish)
                    }
                }

                dbRef.child("Poultry").child("Dishes").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (postSnapshot in dataSnapshot.children) {
                            val dish = postSnapshot.getValue<Dish>()
                            if (dish != null) {
                                dishes.add(dish)
                            }
                        }
                        dbRef.child("Pork").child("Dishes").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {

                                for (postSnapshot in dataSnapshot.children) {
                                    val dish = postSnapshot.getValue<Dish>()
                                    if (dish != null) {
                                        dishes.add(dish)
                                    }
                                }
                                dbRef.child("Seafood").child("Dishes").addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                                        for (postSnapshot in dataSnapshot.children) {

                                            val dish = postSnapshot.getValue<Dish>()
                                            if (dish != null) {
                                                dishes.add(dish)
                                            }
                                        }
                                        adapter.dishes = dishes
                                        adapter.notifyDataSetChanged()
                                    }


                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Toast.makeText(baseContext, databaseError.message, Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Toast.makeText(baseContext, databaseError.message, Toast.LENGTH_SHORT).show()
                            }
                        })
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(baseContext, databaseError.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(baseContext, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    /*                dishes.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val dish = postSnapshot.getValue<Dish>()
                    if (dish != null) {
                        dishes.add(dish)
                    }
                }
                adapter.dishes = dishes
                adapter.notifyDataSetChanged()*/

    override fun onDishClick(dish: Dish) {
        val intent = Intent(this, OrderActivity::class.java)
        intent.putExtra("category", dish.category)
        intent.putExtra("dish",dish.name)
        intent.putExtra("image", dish.image)
        intent.putExtra("price",dish.price)
        intent.putExtra("option",optionModel)
        intent.putExtra("isSecondDish", true)
        intent.putExtra("finalOrder",finalOrder)
        startActivity(intent)
    }

    override fun onFavClick(dish: Dish) {
        myDbRef.child("Favorites").child(FirebaseAuth.getInstance().currentUser.uid).child(dish.name).setValue(dish).addOnSuccessListener {
            Toast.makeText(baseContext,dish.name+" added to Favorites",Toast.LENGTH_SHORT).show()
        }       }
}