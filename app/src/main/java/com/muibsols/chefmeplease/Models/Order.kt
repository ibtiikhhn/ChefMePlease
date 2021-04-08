package com.muibsols.chefmeplease.Models

import android.hardware.SensorAdditionalInfo
import java.io.Serializable

data class Order(
        var orderId: String? = "",
        var dish: Dish? = Dish(),
        var toppings: ArrayList<Topping>? = ArrayList(),
        var sauces: ArrayList<Sauce>? = ArrayList(),
        var spices: ArrayList<Spice>? = ArrayList(),
        var cookStyle: CookStyle? = CookStyle(),
        var beverage: BeverageModel? = BeverageModel(),
        var additionalInfo:String?="No Additional Info",
        var price :String="",
        var tortilla:String="",
        var smoothie:BeverageModel= BeverageModel(),
        var bun :ArrayList<Sauce>?=ArrayList(),
        var cheese:ArrayList<Spice>?= ArrayList()
) : Serializable