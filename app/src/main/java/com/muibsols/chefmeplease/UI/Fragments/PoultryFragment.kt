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
import com.muibsols.chefmeplease.Adapters.OptionAdapter
import com.muibsols.chefmeplease.Listeners.DishClickListener
import com.muibsols.chefmeplease.Listeners.FavClickListener
import com.muibsols.chefmeplease.Listeners.OptionClickListener
import com.muibsols.chefmeplease.Models.Dish
import com.muibsols.chefmeplease.Models.OptionModel
import com.muibsols.chefmeplease.R
import com.muibsols.chefmeplease.UI.Activities.OrderActivity

class PoultryFragment : Fragment(),DishClickListener,FavClickListener, OptionClickListener {

    val TAG = "CHICKEN"
    lateinit var chickenRV:RecyclerView
    lateinit var adapter :DishAdapter
    lateinit var optionsRV:RecyclerView
    lateinit var optionAdapter: OptionAdapter
    val options = ArrayList<OptionModel>()
    var optionModel = OptionModel("Option 1", 1, false, 3)

    val dishes = ArrayList<Dish>()

    val database = Firebase.database
    val myDbRef = database.reference
    val dbRef = database.getReference("Categories").child("Poultry").child("Dishes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chicken, container, false)
        initViews(view)
        return view
    }

    fun initViews(view: View) {
        chickenRV = view.findViewById(R.id.chickenRV)
        chickenRV.layoutManager=GridLayoutManager(context,2)
        adapter = DishAdapter(context,this,this)
        chickenRV.adapter = adapter
        optionsRV = view.findViewById(R.id.optionRV)
        optionsRV.layoutManager = GridLayoutManager(context, 2)
        optionAdapter = OptionAdapter(context,this)
        optionsRV.adapter = optionAdapter

        val option1 = OptionModel("Option 1",1,false,3)
        val option2 = OptionModel("Option 2",2,true,2)
        options.clear()
        options.add(option1)
        options.add(option2)

        optionAdapter.options = options
        optionAdapter.notifyDataSetChanged()
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
        val intent = Intent(this.activity, OrderActivity::class.java)
        intent.putExtra("category", dish.category)
        intent.putExtra("dish",dish.name)
        intent.putExtra("image", dish.image)
        intent.putExtra("price",dish.price)
        intent.putExtra("option",optionModel)
        startActivity(intent)
    }

    override fun onFavClick(dish: Dish) {
        myDbRef.child("Favorites").child(FirebaseAuth.getInstance().currentUser.uid).child(dish.name).setValue(dish).addOnSuccessListener {
            Toast.makeText(context,dish.name+" added to Favorites",Toast.LENGTH_SHORT).show()
        }    }

    override fun onOptionClick(optionModel: OptionModel) {
        this.optionModel = optionModel
    }

}