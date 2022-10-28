package com.ds_create.storeads.activities

import android.content.Intent
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
        onClickClear()
        getFilter()
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

    private fun getFilter() = with(binding) {
        val filter = intent.getStringExtra(FILTER_KEY)
        if (filter != null && filter != EMPTY) {
            val filterArray = filter.split(UNDERSCORE)
            if (filterArray[0] != EMPTY) tvCountry.text = filterArray[0]
            if (filterArray[1] != EMPTY) tvCity.text = filterArray[1]
            if (filterArray[2] != EMPTY) edIndex.setText(filterArray[2])
            checkBoxWithSend.isChecked = filterArray[3].toBoolean()
        }
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
           val intent = Intent().apply {
               putExtra(FILTER_KEY, createFilter())
           }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun onClickClear() = with(binding) {
        btClearFilter.setOnClickListener {
            tvCountry.text = getString(R.string.select_country)
            tvCity.text = getString(R.string.select_city)
            edIndex.setText("")
            checkBoxWithSend.isChecked = false
            setResult(RESULT_CANCELED)
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
                if (index != arrayTempFilter.size - 1)
                    stringBuilder.append(UNDERSCORE)
            } else {
                stringBuilder.append(EMPTY)
                if (index != arrayTempFilter.size - 1)
                    stringBuilder.append(UNDERSCORE)
            }
        }
        return stringBuilder.toString()
    }

    companion object {
        private const val UNDERSCORE = "_"
        private const val EMPTY = "empty"
        const val FILTER_KEY = "filter_key"
    }
}