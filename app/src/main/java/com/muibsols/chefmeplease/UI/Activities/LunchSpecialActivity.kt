package com.muibsols.chefmeplease.UI.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.muibsols.chefmeplease.Adapters.BeverageAdapter
import com.muibsols.chefmeplease.Listeners.BeverageClickListener
import com.muibsols.chefmeplease.Models.*
import com.muibsols.chefmeplease.R
import de.hdodenhof.circleimageview.CircleImageView

class LunchSpecialActivity : AppCompatActivity(),BeverageClickListener{

    lateinit var radioGroup: RadioGroup
    lateinit var dishIMG: CircleImageView
    lateinit var coverIMG: ImageView
    lateinit var dishName: TextView
    lateinit var placeOrder: Button
    lateinit var extraTV:TextView
    lateinit var favIMG: ImageView
    lateinit var smoothieRV: RecyclerView
    lateinit var backBT: FloatingActionButton
    lateinit var additionalInfo: EditText
    lateinit var receivedIntent: Intent
    var selectedBeverage :String="Mango Pineapple"
    lateinit var beverageAdapter: BeverageAdapter
    lateinit var chooseTortillaTV:TextView

    val beverageModels =ArrayList<BeverageModel>()

    val database = Firebase.database
    val firebaseAuth = FirebaseAuth.getInstance()

    var tortilaOption = "Corn"


    private lateinit var dbRef: DatabaseReference
    private lateinit var orderDBRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lunch_special)
        receivedIntent = intent
        val category = receivedIntent.getStringExtra("category")
        val dishName = receivedIntent.getStringExtra("dish")
        val image = receivedIntent.getStringExtra("image")
        val price = receivedIntent.getStringExtra("price")
        val extra = receivedIntent.getStringExtra("extra")

        dbRef = database.reference
        orderDBRef = database.reference

        val dish = Dish(dishName?:"",price?:"",category?:"",image?:"")
        initViews()
        if (dishName.equals("Chicken & Waffles")) {
            chooseTortillaTV.visibility = View.GONE
            radioGroup.visibility = View.GONE
            this.extraTV.text = "3 Buttermilk Tenders, Belgium Waffle and Comes with Smoothie"
        }else{
            this.extraTV.text = "Comes with lettuce, tomato, cheese and sour cream\n" +
                    "Included: Black Beans, Rice, Refried Beans,Salsa & Smoothie"
        }

        Glide.with(baseContext).load(image).fitCenter().into(dishIMG)
        Glide.with(baseContext).load(image).into(coverIMG)
        this.dishName.text = dishName

        placeOrder.setOnClickListener {
            val dish = Dish(dishName ?: "null", price ?: "null", category ?: "null", image ?: "null")

            val id = orderDBRef.root.child("Users").child(firebaseAuth.currentUser.uid).child("Orders").push().key

            val order = Order()
            order.orderId = id
            order.dish=dish
            if (!dish.name.equals("Chicken & Waffles")) {
                order.tortilla = tortilaOption
            }
            order.smoothie = BeverageModel(selectedBeverage,"0")
            order.additionalInfo = additionalInfo.text.toString()
            order.price = dish.price

            val orders = ArrayList<Order>()
            orders.add(order)
            val finalOrder = FinalOrder(orders)

            if (id != null) {
                val intent = Intent(this, OrderConfirmationActivity::class.java)
                intent.putExtra("ORDER", finalOrder)
                startActivity(intent)
            }

        }

        backBT.setOnClickListener{
            finish()
        }

        favIMG.setOnClickListener {
            orderDBRef.child("Favorites").child(FirebaseAuth.getInstance().currentUser.uid).child(dishName?:"").setValue(dish).addOnSuccessListener {
                Toast.makeText(baseContext,dishName+" added to Favorites", Toast.LENGTH_SHORT).show()
            }
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
            tortilaOption = radio.text.toString()
        }

    }

    fun initViews(){
        dishIMG = findViewById(R.id.lsdishIMG)
        dishName = findViewById(R.id.lsdishNameTV)
        coverIMG = findViewById(R.id.lscoverIMG)
        favIMG = findViewById(R.id.lsFav)
        placeOrder = findViewById(R.id.lsplaceOrderBT)
        backBT = findViewById(R.id.lsBackBT)
        smoothieRV = findViewById(R.id.smoothieRV)
        additionalInfo = findViewById(R.id.lsadditionalInsET)
        extraTV = findViewById(R.id.extraTV)
        radioGroup = findViewById(R.id.tortilaOptions)
        chooseTortillaTV = findViewById(R.id.chooseTortillaTV)


        smoothieRV.layoutManager = GridLayoutManager(baseContext,2)
        beverageAdapter= BeverageAdapter(baseContext,this)
        smoothieRV.adapter = beverageAdapter

        val beverageModel1 = BeverageModel("Mango Pineapple","0")
        val beverageModel2 = BeverageModel("Strawberry Banana","0")
        beverageModels.add(beverageModel1)
        beverageModels.add(beverageModel2)
        beverageAdapter.beverages=beverageModels
        beverageAdapter.notifyDataSetChanged()

    }

    override fun onBeverageClick(beverageModel: BeverageModel) {
        selectedBeverage = beverageModel.name
    }
}