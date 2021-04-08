package com.muibsols.chefmeplease.Models

import java.io.Serializable

data class Sauce(var name: String="", var category: String="", var price: String="", var selected: Boolean=false):
    Serializable
