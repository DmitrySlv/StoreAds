package com.ds_create.storeads.utils

import android.content.Context
import com.ds_create.storeads.R
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*

object CityHelper {

    fun getAllCountries(context: Context): ArrayList<String> {
        val tempArray = ArrayList<String>()
        try {
            val inputStream: InputStream = context.assets.open("countriesToCities.json")
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

    fun getAllCities(context: Context, country: String): ArrayList<String> {
        val tempArray = ArrayList<String>()
        try {
            val inputStream: InputStream = context.assets.open("countriesToCities.json")
            val size: Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val cityNames = jsonObject.getJSONArray(country)
                for (n in 0 until cityNames.length()) {
                    tempArray.add(cityNames.getString(n))
            }
        } catch (e: IOException) {

        }
        return tempArray
    }

    fun filterListData(
        list: ArrayList<String>, searchText: String?, context: Context
    ): ArrayList<String> {
        val tempList = ArrayList<String>()
        tempList.clear()
        if (searchText == null) {
            tempList.add(context.getString(R.string.no_result))
            return tempList
        }
        for (selection: String in list) {
            if (selection.lowercase(Locale.ROOT).startsWith(searchText.lowercase(Locale.ROOT)))
                tempList.add(selection)
        }
        if (tempList.size == 0) tempList.add(context.getString(R.string.no_result))
        return tempList
    }

}