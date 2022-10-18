package com.ds_create.storeads.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ds_create.storeads.R
import com.ds_create.storeads.databinding.ActivityDescriptionBinding

class DescriptionActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDescriptionBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}