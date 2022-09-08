package com.ds_create.storeads.utils

import android.app.Application
import com.ds_create.storeads.R
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

object CityHelper {

    fun getAllCountries(application: Application): ArrayList<String> {
        var tempArray = ArrayList<String>()
        try {
            val inputStream: InputStream = application.assets.open("countriesToCities.json")
            val size: Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val countriesNames = jsonObject.names()
            countriesNames?.let {
                for (n in 0 until countriesNames.length()) {
                    tempArray.add(countriesNames.getString(n))
                }
            }
        } catch (e: IOException) {

        }
        return tempArray
    }

    fun filterListData(
        list: ArrayList<String>, searchText: String?, application: Application
    ): ArrayList<String> {
        val tempList = ArrayList<String>()
        tempList.clear()
        if (searchText == null) {
            tempList.add(application.getString(R.string.no_result))
            return tempList
        }
        for (selection: String in list) {
            if (selection.lowercase(Locale.ROOT).startsWith(searchText.lowercase(Locale.ROOT)))
                tempList.add(selection)
        }
        if (tempList.size == 0) tempList.add(application.getString(R.string.no_result))
        return tempList
    }

}