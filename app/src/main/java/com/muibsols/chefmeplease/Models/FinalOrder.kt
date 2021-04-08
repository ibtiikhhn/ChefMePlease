package com.muibsols.chefmeplease.Models

import java.io.Serializable

data class FinalOrder(
    var mainOrder: ArrayList<Order>? = ArrayList(),
    var sides:ArrayList<Side>?=ArrayList(),
    var user: User? = null,
    var date: String? = null,
    var time: String? = null,
    var name: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var city: String? = null,
    var address: String? = null,
    var price: String? = null,
    var orderStatus: String? = null,
    var id :String?=null
):Serializable
