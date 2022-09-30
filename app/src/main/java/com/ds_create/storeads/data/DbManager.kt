package com.ds_create.storeads.data

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager {
    val database = Firebase.database.getReference("main")

    fun publishAd() {
        database.setValue("Ja2")
    }
}