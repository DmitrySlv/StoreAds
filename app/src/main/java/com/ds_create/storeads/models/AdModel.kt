package com.ds_create.storeads.models

import java.io.Serializable

data class AdModel(
    val country: String? = null,
    val city: String? = null,
    val phone: String? = null,
    val index: String? = null,
    val withSent: String? = null,
    val category: String? = null,
    val title: String? = null,
    val price: String? = null,
    val description: String? = null,
    val email: String? = null,
    val mainImage: String = "empty",
    val image2: String = "empty",
    val image3: String = "empty",
    val key: String? = null,
    val uid: String? = null,
    var time: String = "0",
    var favouriteCounter: String = "0",
    var isFavourite: Boolean = false,

    var viewsCounter: String = "0",
    var emailCounter: String = "0",
    var callsCounter: String = "0"
): Serializable
