package com.ds_create.storeads.data

import android.util.Log
import com.ds_create.storeads.models.AdModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

    fun readDataFromDb() {
        database.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children) {
                    val ad = item.children.iterator().next().child("ad").getValue(AdModel::class.java)
                    Log.d("MyLog", "Data: ${ad?.country}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}