package com.ds_create.storeads.data

import com.ds_create.storeads.models.AdModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager {
    val database = Firebase.database.getReference("main")
    val auth = Firebase.auth

    fun publishAd(ad: AdModel) {
        if (auth.uid != null) {
            database.child(ad.key ?: "empty")
                .child(auth.uid!!).child("ad").setValue(ad)
        }
    }
}