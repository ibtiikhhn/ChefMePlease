package com.muibsols.chefmeplease.Listeners

import com.muibsols.chefmeplease.Models.Dish
import com.muibsols.chefmeplease.Models.Sauce
import com.muibsols.chefmeplease.Models.Topping

interface SauceClickListener {
    fun onClickAddSauce(sauce: Sauce)
    fun onClickRemoveSauce(sauce: Sauce)
}