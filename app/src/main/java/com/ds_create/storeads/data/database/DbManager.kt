package com.ds_create.storeads.data.database

import android.widget.Toast
import com.ds_create.storeads.R
import com.ds_create.storeads.activities.EditAdsActivity
import com.ds_create.storeads.models.AdModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager() {
    val database = Firebase.database.getReference("main")
    val auth = Firebase.auth
    private val act: EditAdsActivity? = null

    fun publishAd(ad: AdModel, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null) {
            database.child(ad.key ?: "empty")
                .child(auth.uid!!).child("ad").setValue(ad)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        finishWorkListener.onFinishWork()
                    } else {
                        throw FirebaseException(act?.getString(R.string.firebase_error_load_data).toString())
                    }
                }
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

    interface FinishWorkListener {
        fun onFinishWork()
    }
}