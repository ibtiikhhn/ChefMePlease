package com.muibsols.chefmeplease.Models

import java.io.Serializable

data class CookStyle(val name: String,val category: String) : Serializable {
    constructor():this(name = "",category="")
}