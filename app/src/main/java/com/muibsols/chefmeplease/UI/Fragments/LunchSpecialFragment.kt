package com.muibsols.chefmeplease.UI.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.muibsols.chefmeplease.Listeners.DishClickListener
import com.muibsols.chefmeplease.Listeners.FavClickListener
import com.muibsols.chefmeplease.Models.Dish
import com.muibsols.chefmeplease.R
import com.muibsols.chefmeplease.UI.Activities.BuildBurgerActivity
import com.muibsols.chefmeplease.UI.Activities.LunchSpecialActivity
import com.muibsols.chefmeplease.UI.Activities.OrderActivity


class LunchSpecialFragment : Fragment(),DishClickListener,FavClickListener{

    val TAG = "CHICKEN"
    lateinit var chickenRV:RecyclerView
    lateinit var adapter :DishAdapter

    val dishes = ArrayList<Dish>()

    val database = Firebase.database
    val myDbRef = database.reference
    val dbRef = database.getReference("Categories").child("Lunch Specials").child("Dishes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_speciality, container, false)
        initViews(view)
        return view
    }

    fun initViews(view: View) {
        chickenRV = view.findViewById(R.id.chickenRV)
        chickenRV.layoutManager=GridLayoutManager(context,2)
        adapter = DishAdapter(context,this,this)
        chickenRV.adapter = adapter
        getDishes()
    }

    fun getDishes() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dishes.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val dish = postSnapshot.getValue<Dish>()
                    Log.i(TAG, "onDataChange: "+dish.toString())
                    if (dish != null) {
                        dishes.add(dish)
                    }
                }
                adapter.dishes = dishes
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDishClick(dish: Dish) {
        if (dish.name.equals("Double Bacon Cheeseburger")) {
            val intent = Intent(this.activity, BuildBurgerActivity::class.java)
            intent.putExtra("category", dish.category)
            intent.putExtra("dish",dish.name)
            intent.putExtra("image", dish.image)
            intent.putExtra("price",dish.price)
            intent.putExtra("extra","3 Buttermilk Tenders, Belgium Waffle and Comes with Smoothie")
            startActivity(intent)
        }else{
            val intent = Intent(this.activity, LunchSpecialActivity::class.java)
            intent.putExtra("category", dish.category)
            intent.putExtra("dish",dish.name)
            intent.putExtra("image", dish.image)
            intent.putExtra("price",dish.price)
            intent.putExtra("extra","3 Buttermilk Tenders, Belgium Waffle and Comes with Smoothie")
            startActivity(intent)
        }

    }

    override fun onFavClick(dish: Dish) {
        myDbRef.child("Favorites").child(FirebaseAuth.getInstance().currentUser.uid).child(dish.name).setValue(dish).addOnSuccessListener {
            Toast.makeText(context,dish.name+" added to Favorites",Toast.LENGTH_SHORT).show()
        }    }

}