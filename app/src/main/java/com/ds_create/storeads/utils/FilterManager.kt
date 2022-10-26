package com.ds_create.storeads.utils

import com.ds_create.storeads.models.AdFilterModel
import com.ds_create.storeads.models.AdModel

object FilterManager {
    fun createFilter(ad: AdModel): AdFilterModel {
        return AdFilterModel(
            ad.time,
            "${ad.category}_${ad.time}",
            "${ad.category}_${ad.country}_${ad.withSent}_${ad.time}",
            "${ad.category}_${ad.country}_${ad.city}_${ad.withSent}_${ad.time}",
            "${ad.category}_${ad.country}_${ad.city}_${ad.index}" +
                    "_${ad.withSent}_${ad.time}",
            "${ad.category}_${ad.index}_${ad.withSent}_${ad.time}",
            "${ad.category}_${ad.withSent}_${ad.time}",
            //Filter without category
            "${ad.country}_${ad.withSent}_${ad.time}",
            "${ad.country}_${ad.city}_${ad.withSent}_${ad.time}",
            "${ad.country}_${ad.city}_${ad.index}" +
                    "_${ad.withSent}_${ad.time}",
            "${ad.index}_${ad.withSent}_${ad.time}",
            "${ad.withSent}_${ad.time}"
        )
    }

    fun getFilter(filter: String): String {
        val stringBuilder = StringBuilder()
        val tempArray = filter.split(UNDERSCORE)
        if (tempArray[0] != EMPTY) stringBuilder.append(COUNTRY_)
        if (tempArray[1] != EMPTY) stringBuilder.append(CITY_)
        if (tempArray[2] != EMPTY) stringBuilder.append(INDEX_)
        stringBuilder.append(WITH_SENT_TIME)
        return stringBuilder.toString()
    }

    private const val UNDERSCORE = "_"
    private const val EMPTY = "empty"
    private const val COUNTRY_ = "country_"
    private const val CITY_ = "city_"
    private const val INDEX_ = "index_"
    private const val WITH_SENT_TIME = "withSent_time"
}