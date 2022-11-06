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
                        .setValue(adFilter).addOnCompleteListener {
                            listener.onFinishWork(it.isSuccessful)
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

   private fun addToFavourites(ad: AdModel, finishWorkListener: FinishWorkListener) {
        ad.key?.let { key ->
            auth.uid?.let { uid ->
                database.child(key).child(FAVOURITES_NODE).child(uid).setValue(uid)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            finishWorkListener.onFinishWork(true)
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
                            finishWorkListener.onFinishWork(true)
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

   private fun getAllAdsByFilterNextPage(
       tempFilter: String, time: String, readCallback: ReadDataCallback?
   ) {
        val orderBy = tempFilter.split(VERTICAL_BAR)[0]
        val filter = tempFilter.split(VERTICAL_BAR)[1]
        val query = database.orderByChild("/adFilter/$orderBy")
            .endBefore(filter + "_$time").limitToLast(ADS_LIMIT)
       readDataNextPageFromDb(readCallback, filter, orderBy, query)
    }

    fun getAllAdsNextPage(time: String, filter: String, readCallback: ReadDataCallback?) {
       if (filter.isEmpty()) {
           val query = database.orderByChild(AD_FILTER_TIME_PATH)
           readDataFromDb(readCallback, query)
        } else {
            getAllAdsByFilterNextPage(filter, time,readCallback)
        }

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

    fun getAllAdsFromCatNextPage(
        cat: String, time: String, filter: String, readCallback: ReadDataCallback?
    ) {
        if (filter.isEmpty()) {
            val query = database.orderByChild(AD_FILTER_CAT_TIME_PATH)
                .endBefore(cat + UNDERSCORE + time).limitToLast(ADS_LIMIT)
            readDataFromDb(readCallback, query)
        } else {
            getAllAdsFromCatByFilterNextPage(cat, time, filter, readCallback)
        }
    }

   private fun getAllAdsFromCatByFilterNextPage(
        cat: String, time: String, tempFilter: String, readCallback: ReadDataCallback?
    ) {
        val orderBy = CAT_ + tempFilter.split(VERTICAL_BAR)[0]
        val filter = cat + UNDERSCORE + tempFilter.split(VERTICAL_BAR)[1]
        val query = database.orderByChild("/adFilter/$orderBy")
            .endBefore(filter + UNDERSCORE + time).limitToLast(ADS_LIMIT)
        readDataNextPageFromDb(readCallback,filter, orderBy, query)
    }

    fun deleteAd(ad: AdModel, listener: FinishWorkListener) {
        if (ad.key == null || ad.uid == null) {
            return
        }
        val map = mapOf(
            "/adFilter" to null,
            "/info" to null,
            "/favourites" to null,
            "/${ad.uid}" to null
        )
        database.child(ad.key).updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                listener.onFinishWork(true)
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

    private fun readDataNextPageFromDb(
        readCallback: ReadDataCallback?, filter: String, orderBy: String, query: Query
    ) {
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
                    val filterNodeValue = item.child(FILTER_NODE)
                        .child(orderBy).value.toString()

                    val favouriteCounter = item.child(FAVOURITES_NODE).childrenCount
                    val isFavourite = auth.uid?.let {
                        item.child(FAVOURITES_NODE).child(it).getValue(String::class.java)
                    }
                    ad?.isFavourite = isFavourite != null
                    ad?.favouriteCounter = favouriteCounter.toString()

                    ad?.viewsCounter = infoItem?.viewsCounter ?: DEF_COUNT_INFO_ITEM
                    ad?.emailCounter = infoItem?.emailsCounter ?: DEF_COUNT_INFO_ITEM
                    ad?.callsCounter = infoItem?.callsCounter ?: DEF_COUNT_INFO_ITEM
                    if (ad != null && filterNodeValue.startsWith(filter)) {
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
        fun onFinishWork(isDone: Boolean)
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

