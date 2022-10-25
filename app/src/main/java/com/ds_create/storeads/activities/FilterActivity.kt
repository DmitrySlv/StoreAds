package com.ds_create.storeads.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ds_create.storeads.R
import com.ds_create.storeads.databinding.ActivityFilterBinding
import com.ds_create.storeads.utils.CityHelper
import com.ds_create.storeads.utils.dialogs.DialogSpinnerHelper

class FilterActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFilterBinding.inflate(layoutInflater) }
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        actionBarSettings()
        onClickSelectCountry()
        onClickSelectCity()
        onClickDone()
    }

    private fun actionBarSettings() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    //OnClicks
    private fun onClickSelectCountry() = with(binding) {
        tvCountry.setOnClickListener {
            val listCountry = CityHelper.getAllCountries(this@FilterActivity)
            dialog.showSpinnerDialog(this@FilterActivity, listCountry, tvCountry)
            if (tvCity.text.toString() != getString(R.string.select_city)) {
                tvCity.text = getString(R.string.select_city)
            }
        }
    }

    private fun onClickSelectCity() = with(binding) {
        tvCity.setOnClickListener {
            val selectedCountry = tvCountry.text.toString()
            if (selectedCountry != getString(R.string.select_country)) {
                val listCity = CityHelper.getAllCities(this@FilterActivity, selectedCountry)
                dialog.showSpinnerDialog(this@FilterActivity, listCity, tvCity)
            } else {
                Toast.makeText(
                    this@FilterActivity,
                    getString(R.string.no_country_selected), Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun onClickDone() = with(binding) {
        btDone.setOnClickListener {
            Log.d("MyLog", "Filter: ${createFilter()}")
        }
    }

    private fun createFilter(): String = with(binding) {
        val stringBuilder = StringBuilder()
        val arrayTempFilter = listOf(
            tvCountry.text, tvCity.text, edIndex.text, checkBoxWithSend.isChecked.toString()
        )
        for ((index, string) in arrayTempFilter.withIndex()) {
            if (string != getString(R.string.select_country) &&
                string != getString(R.string.select_city) &&
                    string.isNotEmpty()) {
                stringBuilder.append(string)
                if (index != arrayTempFilter.size - 1) {
                    stringBuilder.append(PROBEL)
                }
            }
        }
        return stringBuilder.toString()
    }

    companion object {
        private const val PROBEL = "_"
    }
}