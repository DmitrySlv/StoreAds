package com.ds_create.storeads.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ds_create.storeads.databinding.ActivityEditAdsBinding

class EditAdsActivity : AppCompatActivity() {

    private val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}