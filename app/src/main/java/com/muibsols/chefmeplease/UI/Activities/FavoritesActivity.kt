package com.muibsols.chefmeplease.UI.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
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
import com.muibsols.chefmeplease.Adapters.DishAdapter
import com.muibsols.chefmeplease.Adapters.FavAdapter
import com.muibsols.chefmeplease.Adapters.OrderAdapter
import com.muibsols.chefmeplease.Listeners.DishClickListener
import com.muibsols.chefmeplease.Listeners.FavClickListener
import com.muibsols.chefmeplease.Models.Dish
import com.muibsols.chefmeplease.Models.OptionModel
import com.muibsols.chefmeplease.Models.Order
import com.muibsols.chefmeplease.R

class FavoritesActivity : AppCompatActivity(),DishClickListener,FavClickListener{

    lateinit var bactBT: ImageView
    val favDishes = ArrayList<Dish>()
    lateinit var favRV: RecyclerView
    lateinit var adapter: FavAdapter
    val database = Firebase.database
    val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var dbRef: DatabaseReference
    var optionModel = OptionModel("Option 1", 1, false, 3)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        initViews()
        val userId = firebaseAuth.currentUser?.uid
        dbRef = database.reference

        dbRef.child("Favorites").child(userId?:"").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                favDishes.clear()
                for (postSnapshot in snapshot.children) {
                    val dish = postSnapshot.getValue<Dish>()
                    if (dish != null) {
                        favDishes.add(dish)
                    }
                }
                adapter.dishes = favDishes
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
        favRV = findViewById(R.id.favDishesRV)
        bactBT = findViewById(R.id.FavBackBT)
        favRV.layoutManager= GridLayoutManager(baseContext,2)

        adapter = FavAdapter(baseContext,this,this)
        favRV.adapter = adapter
    }

    override fun onDishClick(dish: Dish) {
        if (dish.category.equals("Lunch Specials")) {
            if (dish.name.equals("Double Bacon Cheeseburger")) {
                val intent = Intent(this, BuildBurgerActivity::class.java)
                intent.putExtra("category", dish.category)
                intent.putExtra("dish",dish.name)
                intent.putExtra("image", dish.image)
                intent.putExtra("price",dish.price)
                intent.putExtra("extra","3 Buttermilk Tenders, Belgium Waffle and Comes with Smoothie")
                startActivity(intent)
            }else{
                val intent = Intent(this, LunchSpecialActivity::class.java)
                intent.putExtra("category", dish.category)
                intent.putExtra("dish",dish.name)
                intent.putExtra("image", dish.image)
                intent.putExtra("price",dish.price)
                intent.putExtra("extra","3 Buttermilk Tenders, Belgium Waffle and Comes with Smoothie")
                startActivity(intent)
            }
        }else{
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("category", dish.category)
            intent.putExtra("dish",dish.name)
            intent.putExtra("image", dish.image)
            intent.putExtra("price",dish.price)
            intent.putExtra("option",optionModel)
            startActivity(intent)
        }
    }

    override fun onFavClick(dish: Dish) {
        dbRef.child("Favorites").child(FirebaseAuth.getInstance().currentUser.uid).child(dish.name).removeValue()
        dbRef.child("Favorites").child(FirebaseAuth.getInstance().currentUser.uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                favDishes.clear()
                for (postSnapshot in snapshot.children) {
                    val dish = postSnapshot.getValue<Dish>()
                    if (dish != null) {
                        favDishes.add(dish)
                    }
                }
                adapter.dishes = favDishes
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })    }
}