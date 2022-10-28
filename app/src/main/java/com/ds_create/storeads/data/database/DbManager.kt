package com.ds_create.storeads.data.database

import com.ds_create.storeads.models.AdModel
import com.ds_create.storeads.models.InfoItemModel
import com.ds_create.storeads.utils.FilterManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DbManager {
    val database = Firebase.database.getReference(MAIN_NODE)
    val databaseStorage = Firebase.storage.getReference(MAIN_NODE)
    val auth = Firebase.auth

    fun publishAd(ad: AdModel, listener: FinishWorkListener) {
        if (auth.uid != null) {
            database.child(ad.key ?: EMPTY_NODE)
                .child(auth.uid!!).child(AD_NODE).setValue(ad)
                .addOnCompleteListener {

                    val adFilter = FilterManager.createFilter(ad)
                    database.child(ad.key ?: EMPTY_NODE).child(FILTER_NODE)
                        .setValue(adFilter)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                listener.onFinishWork()
                            }
                        }
                }
        }
    }

    fun adViewed(ad: AdModel) {
        var counter = ad.viewsCounter.toInt()
        counter++
        if (auth.uid != null) {
            database.child(ad.key ?: EMPTY_NODE)
                .child(INFO_NODE).setValue(InfoItemModel(
                    counter.toString(), ad.emailCounter, ad.callsCounter
                ))
        }
    }

    fun onFavClick(ad: AdModel, finishWorkListener: FinishWorkListener) {
        if (ad.isFavourite) {
            removeFromFavourites(ad, finishWorkListener)
        } else {
            addToFavourites(ad, finishWorkListener)
        }
    }

    fun addToFavourites(ad: AdModel, finishWorkListener: FinishWorkListener) {
        ad.key?.let { key ->
            auth.uid?.let { uid ->
                database.child(key).child(FAVOURITES_NODE).child(uid).setValue(uid)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            finishWorkListener.onFinishWork()
                        }
                    }
            }
        }
    }

   private fun removeFromFavourites(ad: AdModel, finishWorkListener: FinishWorkListener) {
        ad.key?.let { key ->
            auth.uid?.let { uid ->
                database.child(key).child(FAVOURITES_NODE).child(uid).removeValue()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            finishWorkListener.onFinishWork()
                        }
                    }
            }
        }
    }

    fun getMyAds(readCallback: ReadDataCallback?) {
        val query = database.orderByChild(auth.uid + AD_UID_PATH).equalTo(auth.uid)
        readDataFromDb(readCallback, query)
    }

    fun getMyFavourites(readCallback: ReadDataCallback?) {
        val query = database.orderByChild(AD_FAVOURITES_PATH + auth.uid).equalTo(auth.uid)
        readDataFromDb(readCallback, query)
    }

    fun getAllAdsFirstPage(filter: String, readCallback: ReadDataCallback?) {
        val query = if (filter.isEmpty()) {
            database.orderByChild(AD_FILTER_TIME_PATH).limitToLast(ADS_LIMIT)
        } else {
            getAllAdsByFilterFirstPage(filter)
        }
        readDataFromDb(readCallback, query)
    }

    fun getAllAdsByFilterFirstPage(tempFilter: String): Query {
        val orderBy = tempFilter.split(VERTICAL_BAR)[0]
        val filter = tempFilter.split(VERTICAL_BAR)[1]
        return database.orderByChild("/adFilter/$orderBy")
            .startAt(filter).endAt(filter + END_AT_TIME_WITHOUT_).limitToLast(ADS_LIMIT)
    }

    fun getAllAdsNextPage(time: String, readCallback: ReadDataCallback?) {
        val query = database.orderByChild(AD_FILTER_TIME_PATH)
            .endBefore(time).limitToLast(ADS_LIMIT)
        readDataFromDb(readCallback, query)
    }

    fun getAllAdsFromCatFirstPage(cat: String, filter: String, readCallback: ReadDataCallback?) {
        val query = if(filter.isEmpty()) {
            database.orderByChild(AD_FILTER_CAT_TIME_PATH)
                .startAt(cat).endAt(cat + END_AT_TIME).limitToLast(ADS_LIMIT)
        } else {
            getAllAdsFromCatByFilterFirstPage(cat, filter)
        }
        readDataFromDb(readCallback, query)
    }

    fun getAllAdsFromCatByFilterFirstPage(cat: String, tempFilter: String): Query {
        val orderBy = CAT_ + tempFilter.split(VERTICAL_BAR)[0]
        val filter = cat + UNDERSCORE + tempFilter.split(VERTICAL_BAR)[1]
        return database.orderByChild("/adFilter/$orderBy")
            .startAt(filter).endAt(filter + END_AT_TIME_WITHOUT_).limitToLast(ADS_LIMIT)
    }

    fun getAllAdsFromCatNextPage(catTime: String, readCallback: ReadDataCallback?) {
        val query = database.orderByChild(AD_FILTER_CAT_TIME_PATH)
           .endBefore(catTime).limitToLast(ADS_LIMIT)
        readDataFromDb(readCallback, query)
    }

    fun deleteAd(ad: AdModel, listener: FinishWorkListener) {
        if (ad.key == null || ad.uid == null) {
            return
        }
        database.child(ad.key).child(ad.uid).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                listener.onFinishWork()
            }
        }
    }

   private fun readDataFromDb(readCallback: ReadDataCallback?, query: Query) {
        query.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val adArray = ArrayList<AdModel>()
                for (item in snapshot.children) {

                    var ad: AdModel? = null
                    item.children.forEach{
                        if (ad == null) {
                          ad = it.child(AD_NODE).getValue(AdModel::class.java)
                        }
                    }
                    val infoItem = item.child(INFO_NODE).getValue(InfoItemModel::class.java)

                    val favouriteCounter = item.child(FAVOURITES_NODE).childrenCount
                    val isFavourite = auth.uid?.let {
                        item.child(FAVOURITES_NODE).child(it).getValue(String::class.java)
                    }
                    ad?.isFavourite = isFavourite != null
                    ad?.favouriteCounter = favouriteCounter.toString()

                    ad?.viewsCounter = infoItem?.viewsCounter ?: DEF_COUNT_INFO_ITEM
                    ad?.emailCounter = infoItem?.emailsCounter ?: DEF_COUNT_INFO_ITEM
                    ad?.callsCounter = infoItem?.callsCounter ?: DEF_COUNT_INFO_ITEM
                    if (ad != null) {
                        adArray.add(ad!!)
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

    companion object {
        private const val AD_NODE = "ad"
        private const val FILTER_NODE = "adFilter"
        private const val MAIN_NODE = "main"
        private const val INFO_NODE = "info"
        private const val EMPTY_NODE = "empty"
        private const val AD_UID_PATH = "/ad/uid"
        private const val AD_FAVOURITES_PATH = "/favourites/"
        private const val AD_FILTER_TIME_PATH = "/adFilter/time"
        private const val AD_FILTER_CAT_TIME_PATH = "/adFilter/cat_time"
        private const val FAVOURITES_NODE = "favourites"

        private const val ADS_LIMIT = 2
        private const val DEF_COUNT_INFO_ITEM = "0"

        private const val END_AT_TIME = "_\uf8ff"
        private const val END_AT_TIME_WITHOUT_ = "\uf8ff"
        private const val CAT_ = "cat_"
        private const val VERTICAL_BAR = "|"
        private const val UNDERSCORE = "_"
    }


}

