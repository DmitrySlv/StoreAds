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

    fun deleteItem(ad: AdModel) {
        dbManager.deleteAd(ad, object : DbManager.FinishWorkListener {
            override fun onFinishWork() {
                val updatedList = liveAdsData.value
                updatedList?.remove(ad)
                liveAdsData.postValue(updatedList)
            }
        })
    }

    fun adViewed(ad: AdModel) {
        dbManager.adViewed(ad)
    }

    fun onFavouritesClick(ad: AdModel) {
        dbManager.onFavClick(ad, object : DbManager.FinishWorkListener {
            override fun onFinishWork() {
                val updatedList = liveAdsData.value
                val position = updatedList?.indexOf(ad)
                if (position != -1) {
                    position?.let {
                        updatedList[position] = updatedList[position].copy(isFavourite = !ad.isFavourite)
                    }
                }
                liveAdsData.postValue(updatedList)
            }
        })
    }
}