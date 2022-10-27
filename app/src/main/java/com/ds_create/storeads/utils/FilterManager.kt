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
        val stringBuilderNode = StringBuilder()
        val stringBuilderFilter = StringBuilder()
        val tempArray = filter.split(UNDERSCORE)
        if (tempArray[0] != EMPTY) {
            stringBuilderNode.append(COUNTRY_)
            stringBuilderFilter.append("${tempArray[0]}_")
        }
        if (tempArray[1] != EMPTY) {
            stringBuilderNode.append(CITY_)
            stringBuilderFilter.append("${tempArray[1]}_")
        }
        if (tempArray[2] != EMPTY) {
            stringBuilderNode.append(INDEX_)
            stringBuilderFilter.append("${tempArray[2]}_")
        }
        stringBuilderFilter.append(tempArray[3])
        stringBuilderNode.append(WITH_SENT_TIME)
        return "$stringBuilderNode|$stringBuilderFilter"
    }

    private const val UNDERSCORE = "_"
    private const val EMPTY = "empty"
    private const val COUNTRY_ = "country_"
    private const val CITY_ = "city_"
    private const val INDEX_ = "index_"
    private const val WITH_SENT_TIME = "withSent_time"
}