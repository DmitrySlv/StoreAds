package com.ds_create.storeads.utils

import android.app.Application
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

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
}