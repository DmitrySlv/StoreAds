package com.ds_create.storeads.activities


import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
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
    private var ad: AdModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        binding.fbPhone.setOnClickListener {
            call()
        }
        binding.fbEmail.setOnClickListener {
            sendEmail()
        }
    }

    private fun init() = with(binding) {
        imageAdapter = ImageAdapter()
        viewPager.adapter = imageAdapter
        getIntentFromMainAct()
        imageChangeCounter()
    }

    private fun getIntentFromMainAct() {
        ad = intent.getSerializableExtra(AD_NODE) as AdModel
        ad?.let {
            updateUI(ad!!)
        }
    }

    private fun updateUI(ad: AdModel) {
       ImageManager.fillImageArray(ad, imageAdapter)
        fillTextViews(ad)
    }

    private fun fillTextViews(ad: AdModel) = with(binding) {
        tvTitle.text = ad.title
        tvDescription.text = ad.description
        tvEmail.text = ad.email
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

    private fun call() {
        val callUri = getString(R.string.tel_for_intent) + ad?.phone
        val intentCall = Intent(Intent.ACTION_DIAL)
        intentCall.data = callUri.toUri()
        startActivity(intentCall)
    }

    private fun sendEmail() {
        val intentSendEmail = Intent(Intent.ACTION_SEND)
        intentSendEmail.type = SEND_EMAIL_TYPE
        intentSendEmail.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ad?.email))
            putExtra(Intent.EXTRA_SUBJECT, SUBJECT_TEXT)
            putExtra(Intent.EXTRA_TEXT, TEXT)
        }
        try {
            startActivity(Intent.createChooser(intentSendEmail, OPEN_WITH_TEXT))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.text_intent_exception_email), Toast.LENGTH_LONG).show()
        }
    }

    private fun imageChangeCounter() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter = "${position + 1}/${binding.viewPager.adapter?.itemCount}"
                binding.tvImageCounter.text = imageCounter
            }
        })
    }

    companion object {
        const val AD_NODE = "ad"
        private const val SEND_EMAIL_TYPE = "message/rfc822"
        private const val SUBJECT_TEXT = "Объявление"
        private const val TEXT = "Здравствуйте! Меня заинтересовало ваше объявление."
        private const val OPEN_WITH_TEXT = "Открыть с помощью"
    }
}