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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.muibsols.chefmeplease.Adapters.*
import com.muibsols.chefmeplease.Listeners.*
import com.muibsols.chefmeplease.Models.*
import com.muibsols.chefmeplease.R
import de.hdodenhof.circleimageview.CircleImageView

class BuildBurgerActivity : AppCompatActivity() , ToppingClickListener, SauceClickListener,
    SpiceClickListener, CookStyleClickListener, BeverageClickListener {

    val TAG = "ORDERACTIVITY"

    lateinit var dishIMG: CircleImageView
    lateinit var coverIMG: ImageView
    lateinit var dishName: TextView
    lateinit var placeOrder: Button
    lateinit var favIMG: ImageView
    lateinit var toppingRV: RecyclerView
    lateinit var sauceRV: RecyclerView
    lateinit var spiceRV: RecyclerView
    lateinit var cookRV: RecyclerView
    lateinit var beverageRV: RecyclerView
    lateinit var backBT: FloatingActionButton
    lateinit var additionalInfo: EditText

    lateinit var beverageAdapter: BeverageAdapter
    lateinit var toppingAdapter: ToppingAdapter
    lateinit var sauceAdapter: SauceAdapter
    lateinit var spiceAdapter: SpiceAdapter
    lateinit var cookAdapter: CookAdapter

    lateinit var chooseBun: TextView
    lateinit var chooseTopping: TextView
    lateinit var chooseCheeseOptions: TextView
    lateinit var cookedTV: TextView
    lateinit var bevrageTV: TextView


    val beverageModels =ArrayList<BeverageModel>()
    val toppings = ArrayList<Topping>()
    val sauces = ArrayList<Sauce>()
    val spices = ArrayList<Spice>()
    val cookStyles = ArrayList<CookStyle>()

    val selectedToppings = ArrayList<Topping>()
    val selectedSauces = ArrayList<Sauce>()
    val selectedSpices = ArrayList<Spice>()
    var selectedCookStyle: String = ""
    var selectedBeverage :String=""

    lateinit var receivedIntent: Intent
    val database = Firebase.database
    val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var dbRef: DatabaseReference
    private lateinit var orderDBRef: DatabaseReference

    var sauceLimit:Int=0
    var spiceLimit:Int=0
    var toppingLimit:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_build_burger)
        receivedIntent = intent
        val category = receivedIntent.getStringExtra("category")
        val dishName = receivedIntent.getStringExtra("dish")
        val image = receivedIntent.getStringExtra("image")
        val price = receivedIntent.getStringExtra("price")

        val dish = Dish(dishName?:"",price?:"",category?:"",image?:"")

        initViews()

        dbRef = database.getReference("Categories").child(category!!)
        orderDBRef = database.reference
        Glide.with(baseContext).load(image).fitCenter().into(dishIMG)
        Glide.with(baseContext).load(image).into(coverIMG)
        this.dishName.text = dishName
        getData()

        placeOrder.setOnClickListener {
            val dish = Dish(dishName ?: "null", price ?: "null", category ?: "null", image
                ?: "null")

            val id = orderDBRef.root.child("Users").child(firebaseAuth.currentUser.uid).child("Orders").push().key

            val order = Order()
            order.orderId = id
            order.dish = dish
            order.additionalInfo = additionalInfo.text.toString()
            order.price = dish.price
            order.smoothie = BeverageModel(selectedBeverage,"0")
            order.cookStyle = CookStyle(selectedCookStyle,dish.category)
            order.cheese = selectedSpices
            order.bun = selectedSauces
            order.toppings = selectedToppings

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

    }


    fun initViews() {
        dishIMG = findViewById(R.id.bbdishIMG)
        dishName = findViewById(R.id.bbdishNameTV)
        toppingRV = findViewById(R.id.bbtoppingRV)
        coverIMG = findViewById(R.id.bbcoverIMG)
        sauceRV = findViewById(R.id.bbsauceRV)
        spiceRV = findViewById(R.id.bbspiceRV)
        cookRV = findViewById(R.id.bbcookRV)
        favIMG = findViewById(R.id.bbFav)
        placeOrder = findViewById(R.id.bbplaceOrderBT)
        backBT = findViewById(R.id.bbBackBT)
        beverageRV = findViewById(R.id.bbbeverageRV)
        additionalInfo = findViewById(R.id.bbadditionalInsET)
/*        cookedTV = findViewById(R.id.bbcookedTV)
        bevrageTV = findViewById(R.id.beverageTV)*/

        chooseBun = findViewById(R.id.chooseBunTV)
        chooseTopping = findViewById(R.id.chooseToppingsTV)
        chooseCheeseOptions = findViewById(R.id.chooseCheeseOptions)

        beverageRV.layoutManager = GridLayoutManager(baseContext,2)
        beverageAdapter= BeverageAdapter(baseContext,this)
        beverageRV.adapter = beverageAdapter

        val beverageModel1 = BeverageModel("Mango Pineapple","0")
        val beverageModel2 = BeverageModel("Strawberry Banana","0")
        beverageModels.add(beverageModel1)
        beverageModels.add(beverageModel2)
        beverageAdapter.beverages=beverageModels
        beverageAdapter.notifyDataSetChanged()

        toppingRV.layoutManager = GridLayoutManager(baseContext, 2)
        toppingAdapter = ToppingAdapter(baseContext, this,1)
        toppingRV.adapter = toppingAdapter


        sauceRV.layoutManager = GridLayoutManager(baseContext, 2)
        sauceAdapter = SauceAdapter(baseContext, this,100)
        sauceRV.adapter = sauceAdapter

        spiceRV.layoutManager = GridLayoutManager(baseContext, 2)
        spiceAdapter = SpiceAdapter(baseContext, this,100)
        spiceRV.adapter = spiceAdapter

        cookRV.layoutManager = GridLayoutManager(baseContext, 4)
        cookAdapter = CookAdapter(baseContext, this)
        cookRV.adapter = cookAdapter
    }

    fun getData() {
        dbRef?.child("Sauces")?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                toppings.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val topping = postSnapshot.getValue<Topping>()
                    if (topping != null) {
                        toppings.add(topping)
                    }
                }
                toppingAdapter.toppings = toppings
                toppingAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(baseContext, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })

        dbRef?.child("Toppings")?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sauces.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val sauce = postSnapshot.getValue<Sauce>()
                    if (sauce != null) {
                        sauces.add(sauce)
                    }
                }
                sauceAdapter.sauces = sauces
                sauceAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(baseContext, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })

        dbRef?.child("Spices")?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                spices.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val spice = postSnapshot.getValue<Spice>()
                    if (spice != null) {
                        spices.add(spice)
                    }
                }
                spiceAdapter.spices = spices
                spiceAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(baseContext, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })

        dbRef?.child("Cook Style")?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cookStyles.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val cookStyle = postSnapshot.getValue<CookStyle>()
                    if (cookStyle != null) {
                        cookStyles.add(cookStyle)
                    }
                }
                cookAdapter.cookStyles = cookStyles
                cookAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(baseContext, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })

/*        val pepsi = BeverageModel("Pepsi", "1")
        val coke = BeverageModel("Coke","1")
        val sprite = BeverageModel("Sprite","1")
        val sevenUp = BeverageModel("7 Up","1")
        val dew = BeverageModel("Dew","1")

        beverageModels.add(pepsi)
        beverageModels.add(coke)
        beverageModels.add(sprite)
        beverageModels.add(sevenUp)
        beverageModels.add(dew)
        beverageAdapter.beverages = beverageModels
        beverageAdapter.notifyDataSetChanged()*/

    }

    override fun onClickAddTopping(topping: Topping) {
        selectedToppings.add(topping)
    }

    override fun onClickRemoveTopping(topping: Topping) {
        selectedToppings.remove(topping)
    }

    override fun onClickAddSauce(sauce: Sauce) {
        selectedSauces.add(sauce)
    }

    override fun onClickRemoveSauce(sauce: Sauce) {
        selectedSauces.remove(sauce)
    }

    override fun onClickAddSpice(spice: Spice) {
        selectedSpices.add(spice)
    }

    override fun onClickRemoveSpice(spice: Spice) {
        selectedSpices.remove(spice)
    }

    override fun onCookStyleClick(cookStyle: CookStyle) {
        selectedCookStyle = cookStyle.name
    }

    override fun onBeverageClick(beverageModel: BeverageModel) {
        selectedBeverage = beverageModel.name
    }
}