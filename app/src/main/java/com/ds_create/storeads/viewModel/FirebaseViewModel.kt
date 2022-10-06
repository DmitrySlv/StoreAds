package com.ds_create.storeads.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ds_create.storeads.data.database.DbManager
import com.ds_create.storeads.models.AdModel

class FirebaseViewModel: ViewModel() {

    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<AdModel>>()

    fun loadAllAds() {
       dbManager.getAllAds(object : DbManager.ReadDataCallback {
           override fun readData(list: ArrayList<AdModel>) {
               liveAdsData.value = list
           }
       })
    }

    fun loadMyAds() {
        dbManager.getMyAds(object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<AdModel>) {
                liveAdsData.value = list
            }
        })
    }
}