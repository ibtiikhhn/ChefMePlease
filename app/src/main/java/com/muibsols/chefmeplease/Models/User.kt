package com.muibsols.chefmeplease.Models

import java.io.Serializable

data class User(
    var name: String = "null",
    var email: String = "null",
    var id: String = "null",
    var imageUrl: String = "null",
    var phoneNumber: String = "null")
:Serializable