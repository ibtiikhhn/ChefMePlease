package com.muibsols.chefmeplease.UI.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muibsols.chefmeplease.Models.FinalOrder
import com.muibsols.chefmeplease.Models.Order
import com.muibsols.chefmeplease.Models.User
import com.muibsols.chefmeplease.R
import com.muibsols.chefmeplease.UI.Activities.Stripe.FirebaseEphemeralKeyProvider
import com.stripe.android.*
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.view.BillingAddressFields
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.Timepoint
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class OrderConfirmationActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {

    val TAG = "ORDERCONFIRMATION"

    lateinit var dishName: TextView
    lateinit var totalPriceTV: TextView
    lateinit var dishIMG: ImageView
    lateinit var backBT: ImageView
    lateinit var secondIMG: ImageView
    lateinit var datePicker: TextView
    lateinit var timePicker: TextView
    lateinit var nameET: EditText
    lateinit var emailET: EditText
    lateinit var phoneNumberET: EditText
    lateinit var cityET: EditText
    lateinit var addresET: EditText
    lateinit var placeOrderBT: Button
    lateinit var order: FinalOrder
    lateinit var paymentMethodBT: Button
    var date = ""
    var time = ""
    var sumTotal = ""

    val database = Firebase.database
    val firebaseAuth = FirebaseAuth.getInstance()
    lateinit var progressDialog: ProgressDialog

    private lateinit var dbRef: DatabaseReference

    private var currentUser: FirebaseUser? = null
    private lateinit var paymentSession: PaymentSession
    private lateinit var selectedPaymentMethod: PaymentMethod
    private val stripe: Stripe by lazy {
        Stripe(
            applicationContext,
            getString(R.string.stripePublishableKey)
        )
    }

    private val disabledTimepoints = ArrayList<Timepoint>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)
        initViews()
        placeOrderBT.visibility = View.GONE
        currentUser = FirebaseAuth.getInstance().currentUser
        val intentt = intent
        order = intentt.getSerializableExtra("ORDER") as FinalOrder
        progressDialog = ProgressDialog(this)


        if (order.mainOrder?.get(0)?.dish?.category.equals("Lunch Specials")) {
            secondIMG.visibility = View.GONE
            if (order.mainOrder?.get(0)?.dish?.name.equals("Double Bacon Cheeseburger")) {
                var bunTotal = 0.0F
                var cheeseTotal = 0.0F
                var toppingTotal = 0.0F

                val orderrr = order.mainOrder?.get(0)
                for (cheese in orderrr?.cheese!!) {
                    cheeseTotal += cheese.price.toFloat()
                }
                for (bun in orderrr?.bun!!) {
                    bunTotal += bun.price.toFloat()
                }
                for (topping in orderrr.toppings!!) {
                    toppingTotal += topping.price.toFloat()
                }

                val total =
                    bunTotal + cheeseTotal + toppingTotal + orderrr.smoothie.price.toFloat() + orderrr.dish?.price!!.toFloat()
                sumTotal = total.toString()
                totalPriceTV.text = "$ $total"
                Glide.with(baseContext).load(order.mainOrder?.get(0)?.dish?.image).into(dishIMG)
                dishName.text = order.mainOrder?.get(0)?.dish?.name

            } else {
                sumTotal = order.mainOrder?.get(0)?.dish?.price.toString()
                totalPriceTV.text = "$ $sumTotal"
                Glide.with(baseContext).load(order.mainOrder?.get(0)?.dish?.image).into(dishIMG)
                dishName.text = order.mainOrder?.get(0)?.dish?.name
            }
        } else {
            if (order.mainOrder?.size!! == 1) {
                secondIMG.visibility = View.GONE
            }

            val sumTotal = sumTotal(order)
            totalPriceTV.text = "$ $sumTotal"

            var name = ""
            order.mainOrder?.forEach { miniOrder ->
                name += miniOrder.dish?.name + ", "
            }
            name.dropLast(2)
            dishName.text = name
            if (order.mainOrder?.size!! > 1) {
                Glide.with(baseContext).load(order.mainOrder?.get(1)?.dish?.image).into(secondIMG)
            }
            Glide.with(baseContext).load(order.mainOrder?.get(0)?.dish?.image).into(dishIMG)
        }






        dbRef = database.reference
        backBT.setOnClickListener {
            finish()
        }

        setupPaymentSession()

        paymentMethodBT.setOnClickListener {
            paymentSession.presentPaymentMethodSelection()
        }

        placeOrderBT.setOnClickListener {

            if (date.isEmpty()) {
                Toast.makeText(baseContext, "Invalid Date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (time.isEmpty()) {
                Toast.makeText(baseContext, "Invalid Time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (nameET.text.toString().isEmpty()) {
                Toast.makeText(baseContext, "Invalid Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (emailET.text.toString().isEmpty()) {
                Toast.makeText(baseContext, "Invalid Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phoneNumberET.text.toString().isEmpty()) {
                Toast.makeText(baseContext, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (cityET.text.toString().isEmpty()) {
                Toast.makeText(baseContext, "Invalid City", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (addresET.text.toString().isEmpty()) {
                Toast.makeText(baseContext, "Invalid Address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            order.price = sumTotal
            order.date = date
            order.time = time
            order.name = nameET.text.toString()
            order.email = emailET.text.toString()
            order.phone = phoneNumberET.text.toString()
            order.city = cityET.text.toString()
            order.address = addresET.text.toString()
            order.user = User(
                firebaseAuth.currentUser.displayName,
                firebaseAuth.currentUser.email,
                firebaseAuth.currentUser.uid,
                firebaseAuth.currentUser.photoUrl.toString(),
                firebaseAuth.currentUser.phoneNumber
            )
            order.orderStatus = "Pending"

            val id = dbRef.root.child("Users").child(firebaseAuth.currentUser.uid).child("Orders")
                .push().key
            order.id = id

            if (selectedPaymentMethod.id != null) {
                confirmPayment(selectedPaymentMethod.id!!, order)
            } else {
                Toast.makeText(baseContext,"Choose Payment Method",Toast.LENGTH_SHORT).show()
            }

        }

        datePicker.setOnClickListener {
            val MAX_SELECTABLE_DATE_IN_FUTURE = 365; // 365 days into the future max
            val now = Calendar.getInstance()
            now.add(Calendar.DATE,1)
            var dpd = DatePickerDialog.newInstance(
                this,
                now[Calendar.YEAR],  // Initial year selection
                now[Calendar.MONTH],  // Initial month selection
                now[Calendar.DAY_OF_MONTH] // Inital day selection
            )
            dpd.minDate = now
            //Disable all SUNDAYS and SATURDAYS between Min and Max Dates
            val weekdays = ArrayList<Calendar>()
            val day = Calendar.getInstance()
            for (i in 0 until MAX_SELECTABLE_DATE_IN_FUTURE) {
                if (day[Calendar.DAY_OF_WEEK] !== Calendar.SUNDAY) {
                    val d = day.clone() as Calendar
                    weekdays.add(d)
                }
                day.add(Calendar.DATE, 1)
            }
            val weekdayDays: Array<Calendar> = weekdays.toArray(arrayOfNulls(weekdays.size))
            dpd.selectableDays = weekdayDays

            dpd.setOnCancelListener {
                date = ""
                datePicker.text  ="Pick Delivery Date"
            }

            dpd.show(supportFragmentManager, "HELL")

        }

        timePicker.setOnClickListener {
            val minTimepoint = Timepoint(11, 0)
            val maxTimepoint = Timepoint(20, 0)
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            var tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                this, hour,  // Initial year selection
                minute, false// Inital day selection
            )
            tpd.setDisabledTimes(disabledTimepoints.toTypedArray())
            tpd.setMinTime(minTimepoint)
            tpd.setMaxTime(maxTimepoint)

            tpd.setOnCancelListener {
                time = ""
                timePicker.text = "Pick Delivery Time"
            }
            tpd.show(supportFragmentManager, "HELL")
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        paymentSession.handlePaymentData(requestCode, resultCode, data ?: Intent())

    }

    private fun confirmPayment(paymentMethodId: String, order: FinalOrder) {

        progressDialog.setMessage("Processing")
        progressDialog.show()

        val paymentCollection = Firebase.firestore

        val amount = hashMapOf(
            "amount" to sumTotal.toFloat() * 100,
            "currency" to "usd"
        )
        paymentCollection.collection("stripe_customers").document(currentUser?.uid ?: "")
            .collection("payments").add(amount).addOnFailureListener {
            }
            .addOnSuccessListener { documentReference ->
                Log.d("payment", "DocumentSnapshot added with ID: ${documentReference.id}")
                documentReference.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("payment", "Listen failed.", e)
                        progressDialog.dismiss()
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d("payment", "Current data: ${snapshot.data}")
                        val clientSecret = snapshot.data?.get("client_secret")
                        Log.d("payment", "Create paymentIntent returns $clientSecret")
                        clientSecret?.let {
                            stripe.confirmPayment(
                                this, ConfirmPaymentIntentParams.createWithPaymentMethodId(
                                    paymentMethodId,
                                    (it as String)
                                )
                            )

//                            checkoutSummary.text = "Thank you for your payment"
                            Toast.makeText(applicationContext, "Payment Done!!", Toast.LENGTH_LONG)
                                .show()
                            placeOrder(order)
                        }
                    } else {
                        Log.e("payment", "Current payment intent : null")
//                        payButton.isEnabled = true
                        progressDialog.show()
                        placeOrderBT.visibility = View.VISIBLE
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.i(
                    TAG,
                    "confirmPayment: " + e.message
                )//                payButton.isEnabled = true
                progressDialog.dismiss()
            }
    }

    fun placeOrder(order: FinalOrder) {
        dbRef.child("AllOrders").child(order.id ?: "").setValue(order).addOnSuccessListener {
            dbRef.child("UserOrders").child(firebaseAuth.currentUser.uid).child(
                order.id
                    ?: ""
            ).setValue(order).addOnSuccessListener {
                Toast.makeText(baseContext, "Order Placed", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                startActivity(Intent(this, BaseActivity::class.java))
                finish()
            }
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(baseContext, it.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setupPaymentSession() {
// Setup Customer Session
        CustomerSession.initCustomerSession(this, FirebaseEphemeralKeyProvider())
// Setup a payment session
        paymentSession = PaymentSession(
            this, PaymentSessionConfig.Builder()
                .setShippingInfoRequired(false)
                .setShippingMethodsRequired(false)
                .setBillingAddressFields(BillingAddressFields.None)
                .setShouldShowGooglePay(true)
                .build()
        )

        paymentSession.init(
            object : PaymentSession.PaymentSessionListener {
                override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
                    Log.d("PaymentSession", "PaymentSession has changed: $data")
                    Log.d(
                        "PaymentSession",
                        "${data.isPaymentReadyToCharge} <> ${data.paymentMethod}"
                    )

                    if (data.isPaymentReadyToCharge) {
                        Log.d("PaymentSession", "Ready to charge");
//                        payButton.isEnabled = true
                        placeOrderBT.visibility = View.VISIBLE

                        data.paymentMethod?.let {
                            Log.d("PaymentSession", "PaymentMethod $it selected")
//                            paymentmethod.text = "${it.card?.brand} card ends with ${it.card?.last4}"
                            selectedPaymentMethod = it
                        }
                    }
                }

                override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
                    Log.d("PaymentSession", "isCommunicating $isCommunicating")
                }

                override fun onError(errorCode: Int, errorMessage: String) {
                    Log.e("PaymentSession", "onError: $errorCode, $errorMessage")
                    placeOrderBT.visibility = View.GONE
                }
            }
        )

    }

    fun initViews() {
        backBT = findViewById(R.id.OcBackBT)
        datePicker = findViewById(R.id.OcDateET)
        timePicker = findViewById(R.id.OcTimeET)
        nameET = findViewById(R.id.OcNameET)
        emailET = findViewById(R.id.OcEmailET)
        phoneNumberET = findViewById(R.id.OcPhoneNumberET)
        cityET = findViewById(R.id.OcCityET)
        addresET = findViewById(R.id.OcAddressET)
        placeOrderBT = findViewById(R.id.OcPlaceOrderBT)
        dishName = findViewById(R.id.ocDishName)
        dishIMG = findViewById(R.id.ocDishIMG)
        totalPriceTV = findViewById(R.id.subtotalTV)
        secondIMG = findViewById(R.id.secondIMG)
        paymentMethodBT = findViewById(R.id.choosePaymentBT)
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.cancel()
    }

    fun sumTotal(order: FinalOrder): Float {
        var total = 0.0F

        order.mainOrder?.forEach { orderr ->
            total += miniOrderTotal(orderr)
        }


        order.sides?.forEach { side ->
            total += side.price.toFloat()

        }

        sumTotal = total.toString()
        return total
    }

    fun miniOrderTotal(order: Order): Float {
        var sauceTotal = 0.0F
        var spiceTotal = 0.0F
        var toppingTotal = 0.0F

        for (sauce in order.sauces!!) {
            sauceTotal += sauce.price.toFloat()
        }
        for (spice in order.spices!!) {
            spiceTotal += spice.price.toFloat()
        }
        for (topping in order.toppings!!) {
            toppingTotal += topping.price.toFloat()
        }

        return sauceTotal + spiceTotal + toppingTotal + order.dish!!.price.toFloat() + order.beverage!!.price.toFloat()
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, monthOfYear, dayOfMonth)
        val myFormat = "dd.MM.yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        date = sdf.format(calendar.time)
        var orderList= ArrayList<FinalOrder>()
        datePicker.text = date
        time = ""
        timePicker.text = "Pick Delivery Time"
        progressDialog.show()
        disabledTimepoints.clear()
        dbRef.child("AllOrders").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val order = postSnapshot.getValue<FinalOrder>()
                    if (order != null) {
                        if (order.date.toString().equals(date)) {
                            orderList.add(order)
                            if (!order.time.toString().isEmpty()) {
                                findDisabledTimes(order.time.toString())
                            }
                        }
                    }
                }
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
            }
        })

    }

    fun findDisabledTimes(reservedTime: String) {
        val sdf = SimpleDateFormat("hh:mm a")
        val d: Date = sdf.parse(reservedTime)
        var fwdMinute = d.time
        var bckMinute = d.time

        val fwdDate = Date(fwdMinute)
        disabledTimepoints.add(Timepoint(fwdDate.hours,fwdDate.minutes))
        for (i in 1..59) {
            fwdMinute += 60000
            val fwdDate = Date(fwdMinute)
            disabledTimepoints.add(Timepoint(fwdDate.hours,fwdDate.minutes))
        }

        val bckDate = Date(bckMinute)
        disabledTimepoints.add(Timepoint(bckDate.hours,bckDate.minutes))
        for (i in 1..59) {
            bckMinute -= 60000
            val bckDate = Date(bckMinute)
            disabledTimepoints.add(Timepoint(bckDate.hours,bckDate.minutes))
        }


    }



    override fun onTimeSet(
        view: com.wdullaer.materialdatetimepicker.time.TimePickerDialog?,
        hourOfDay: Int,
        minute: Int,
        second: Int
    ) {
        val timee = Time(hourOfDay, minute, second)
        val sdf = SimpleDateFormat("hh:mm a")
        val timew = sdf.format(timee)
        time = timew
        timePicker.text = time
    }


}