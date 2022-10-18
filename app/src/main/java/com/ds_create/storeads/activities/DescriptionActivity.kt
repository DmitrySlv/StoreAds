package com.ds_create.storeads.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ds_create.storeads.R
import com.ds_create.storeads.adapters.ImageAdapter
import com.ds_create.storeads.databinding.ActivityDescriptionBinding
import com.ds_create.storeads.models.AdModel
import com.ds_create.storeads.utils.ImageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DescriptionActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDescriptionBinding.inflate(layoutInflater) }
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    private fun init()= with(binding) {
        imageAdapter = ImageAdapter()
        viewPager.adapter = imageAdapter
        getIntentFromMainAct()
    }

    private fun getIntentFromMainAct() {
        val ad = intent.getSerializableExtra(AD_NODE) as AdModel
        fillImageArray(ad)
    }

    private fun fillImageArray(ad: AdModel) {
        val listUris = listOf(ad.mainImage, ad.image2, ad.image3)
        CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = ImageManager.getBitmapFromUris(listUris)
            imageAdapter.update(bitmapList as ArrayList<Bitmap>)
        }
    }

    companion object {
        const val AD_NODE = "ad"
    }
}