package com.ds_create.storeads.data.database

import com.ds_create.storeads.models.AdModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
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

    fun getMyAds(readCallback: ReadDataCallback?) {
        val query = database.orderByChild(auth.uid + "/ad/uid").equalTo(auth.uid)
        readDataFromDb(readCallback, query)
    }

    fun getAllAds(readCallback: ReadDataCallback?) {
        val query = database.orderByChild(auth.uid + "/ad/price")
        readDataFromDb(readCallback, query)
    }

   private fun readDataFromDb(readCallback: ReadDataCallback?, query: Query) {
        query.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val adArray = ArrayList<AdModel>()
                for (item in snapshot.children) {
                    val ad = item.children.iterator().next()
                            .child("ad")
                            .getValue(AdModel::class.java)
                    if (ad != null) {
                        adArray.add(ad)
                    }
                }
                readCallback?.readData(adArray)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    interface ReadDataCallback {
        fun readData(list: ArrayList<AdModel>)
    }
}