package com.ds_create.storeads.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ds_create.storeads.databinding.ActivityEditAdsBinding
import com.ds_create.storeads.dialogs.DialogSpinnerHelper
import com.ds_create.storeads.utils.CityHelper

class EditAdsActivity : AppCompatActivity() {

    private val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val listCountry = CityHelper.getAllCountries(application)
        val dialog = DialogSpinnerHelper()
        dialog.showSpinnerDialog(this, listCountry, application)
    }
}