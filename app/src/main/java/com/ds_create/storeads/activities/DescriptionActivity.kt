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
        updateUI(ad)
    }

    private fun updateUI(ad: AdModel) {
        fillImageArray(ad)
        fillTextViews(ad)
    }

    private fun fillTextViews(ad: AdModel) = with(binding) {
        tvTitle.text = ad.title
        tvDescription.text = ad.description
        tvPrice.text = ad.price
        tvPhone.text = ad.phone
        tvCountry.text = ad.country
        tvCity.text = ad.city
        tvIndex.text = ad.index
        tvWithSent.text = isWithSent(ad.withSend.toBoolean())
    }

    private fun isWithSent(withSent: Boolean): String {
        return if (withSent) {
            getString(R.string.with_sent_yes)
        } else {
            getString(R.string.with_sent_no)
        }
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