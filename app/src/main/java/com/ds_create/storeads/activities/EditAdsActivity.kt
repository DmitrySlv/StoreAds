package com.ds_create.storeads.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ds_create.storeads.databinding.ActivityEditAdsBinding
import com.ds_create.storeads.dialogs.DialogSpinnerHelper
import com.ds_create.storeads.utils.CityHelper

class EditAdsActivity : AppCompatActivity() {

    val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    private fun init() {
    }

    //OnClicks
    fun onClickSelectCountry(view: View) {
        val listCountry = CityHelper.getAllCountries(application)
        dialog.showSpinnerDialog(this, listCountry)
    }
}