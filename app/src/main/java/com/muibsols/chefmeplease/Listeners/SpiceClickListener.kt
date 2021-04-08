package com.muibsols.chefmeplease.Listeners

import com.muibsols.chefmeplease.Models.Spice
import com.muibsols.chefmeplease.Models.Topping

interface SpiceClickListener {
    fun onClickAddSpice(spice: Spice)
    fun onClickRemoveSpice(spice: Spice)
}