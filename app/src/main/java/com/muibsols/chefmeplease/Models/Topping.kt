package com.muibsols.chefmeplease.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class Topping(var name: String="", var category: String="", var price: String="", var selected: Boolean=false):
    Serializable