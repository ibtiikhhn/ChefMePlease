package com.muibsols.chefmeplease.Listeners

import com.muibsols.chefmeplease.Models.Sauce
import com.muibsols.chefmeplease.Models.Topping

interface ToppingClickListener {
    fun onClickAddTopping(topping: Topping)
    fun onClickRemoveTopping(topping: Topping)
}