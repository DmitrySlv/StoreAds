package com.ds_create.storeads.data

import com.ds_create.storeads.models.AdModel

interface ReadDataCallback {
    fun readData(list: List<AdModel>)
}