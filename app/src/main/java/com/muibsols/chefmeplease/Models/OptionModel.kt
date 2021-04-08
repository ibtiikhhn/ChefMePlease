package com.muibsols.chefmeplease.Models

import java.io.Serializable

data class OptionModel(var name:String="", var totalDishes:Int=0, var saladIncluded:Boolean=false,var sides :Int=0):Serializable
