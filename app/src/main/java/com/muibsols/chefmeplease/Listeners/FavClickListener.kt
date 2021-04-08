package com.muibsols.chefmeplease.Listeners

import com.muibsols.chefmeplease.Models.Dish

interface FavClickListener {
    fun onFavClick(dish: Dish)
}