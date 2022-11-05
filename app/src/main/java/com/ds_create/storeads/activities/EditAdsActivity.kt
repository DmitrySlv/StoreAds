package com.ds_create.storeads.activities

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.ds_create.storeads.R
import com.ds_create.storeads.adapters.ImageAdapter
import com.ds_create.storeads.data.database.DbManager
import com.ds_create.storeads.databinding.ActivityEditAdsBinding
import com.ds_create.storeads.utils.dialogs.DialogSpinnerHelper
import com.ds_create.storeads.fragments.FragmentCloseInterface
import com.ds_create.storeads.fragments.ImageListFrag
import com.ds_create.storeads.models.AdModel
import com.ds_create.storeads.utils.CityHelper
import com.ds_create.storeads.utils.ImageManager
import com.ds_create.storeads.utils.ImagePicker
import com.google.android.gms.tasks.OnCompleteListener
import java.io.ByteArrayOutputStream

class EditAdsActivity : AppCompatActivity(), FragmentCloseInterface {

    val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }
    private val dialog = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapter
    var chooseImageFrag: ImageListFrag? = null
    var editImagePos = 0
    private var imageIndex = 0
    private val dbManager = DbManager()
    private var isEditState = false
    private var ad: AdModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        checkEditState()
        imageChangeCounter()
        onClickSelectCountry()
        onClickSelectCity()
        onClickSelectCat()
        onClickGetImages()
        onClickPublish()
    }

    private fun init() {
        imageAdapter = ImageAdapter()
        binding.vpImages.adapter = imageAdapter
    }

    private fun checkEditState() {
        isEditState = isEditState()
        if (isEditState) {
            ad = intent.getSerializableExtra(MainActivity.ADS_DATA) as AdModel
            ad?.let {
                fillViewsFromFirebase(ad!!)
            }
        }
    }

    private fun isEditState(): Boolean {
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)
    }

    private fun fillViewsFromFirebase(ad: AdModel) = with(binding) {
        tvCountry.text = ad.country
        tvCity.text = ad.city
        edPhone.setText(ad.phone)
        edIndex.setText(ad.index)
        checkBoxWithSend.isChecked = ad.withSent.toBoolean()
        tvCat.text = ad.category
        edTitle.setText(ad.title)
        edPrice.setText(ad.price)
        edDescription.setText(ad.description)
        updateImageCounter(0)
        ImageManager.fillImageArray(ad, imageAdapter)
    }

    //OnClicks
   private fun onClickSelectCountry() = with(binding) {
        tvCountry.setOnClickListener {
            val listCountry = CityHelper.getAllCountries(this@EditAdsActivity)
            dialog.showSpinnerDialog(this@EditAdsActivity, listCountry, tvCountry)
            if (tvCity.text.toString() != getString(R.string.select_city)) {
                tvCity.text = getString(R.string.select_city)
            }
        }

    }

   private fun onClickSelectCity() = with(binding) {
        tvCity.setOnClickListener {
            val selectedCountry = tvCountry.text.toString()
            if (selectedCountry != getString(R.string.select_country)) {
                val listCity = CityHelper.getAllCities(this@EditAdsActivity, selectedCountry)
                dialog.showSpinnerDialog(this@EditAdsActivity, listCity, tvCity)
            } else {
                Toast.makeText(this@EditAdsActivity,
                    getString(R.string.no_country_selected), Toast.LENGTH_LONG).show()
            }
        }
    }

   private fun onClickSelectCat() = with(binding) {
        tvCat.setOnClickListener {
            val listCategory = resources.getStringArray(R.array.category).toMutableList() as ArrayList
            dialog.showSpinnerDialog(this@EditAdsActivity, listCategory, tvCat)
        }

    }

    fun onClickGetImages() {
        binding.ibEditImageAd.setOnClickListener {
            if (imageAdapter.mainArray.size == 0) {
                ImagePicker.getMultiImages(this@EditAdsActivity, ImagePicker.MULTI_IMAGE_COUNTER)
            } else {
                openChooseImageFrag(null)
                chooseImageFrag?.updateAdapterFromEdit(imageAdapter.mainArray)
            }
        }
    }

    fun onClickPublish() = with(binding) {
        btPublish.setOnClickListener {
            progressLayout.visibility = View.VISIBLE
            ad = fillAd()
            uploadImages()
        }
    }

    private fun onPublishFinish(): DbManager.FinishWorkListener {
        return object : DbManager.FinishWorkListener {
            override fun onFinishWork(isDone: Boolean) {
              binding.progressLayout.visibility = View.GONE
               if (isDone) {
                   finish()
               }
            }
        }
    }

    private fun fillAd(): AdModel {
        val adTemp: AdModel
        binding.apply {
            adTemp = AdModel(
                tvCountry.text.toString(),
                tvCity.text.toString(),
                edPhone.text.toString(),
                edIndex.text.toString(),
                checkBoxWithSend.isChecked.toString(),
                tvCat.text.toString(),
                edTitle.text.toString(),
                edPrice.text.toString(),
                edDescription.text.toString(),
                edEmail.text.toString(),
                ad?.mainImage ?: EMPTY,
                ad?.image2 ?: EMPTY,
                ad?.image3 ?: EMPTY,
                ad?.key ?: dbManager.database.push().key,
                dbManager.auth.uid,
                ad?.time ?: System.currentTimeMillis().toString(),
                "0"
            )
        }
        return adTemp
    }

    override fun onFragClose(list: ArrayList<Bitmap>) {
        binding.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageFrag = null
        updateImageCounter(binding.vpImages.currentItem)
    }

    fun openChooseImageFrag(newList: ArrayList<Uri>?) {
        chooseImageFrag = ImageListFrag(this)
        if (newList != null) {
            chooseImageFrag?.resizeSelectedImages(newList, true, this)
        }
        binding.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.placeHolder, chooseImageFrag!!)
        fm.commit()
    }

    private fun uploadImages() {
        if (imageIndex == 3) {
            dbManager.publishAd(ad!!, onPublishFinish())
            return
        }
        val oldUrl = getUrlFromAd()
        if (imageAdapter.mainArray.size > imageIndex) {
            val byteArray = prepareImageByteArray(imageAdapter.mainArray[imageIndex])
            if (oldUrl.startsWith(HTTP)) {
                updateImage(byteArray, oldUrl) {
                    nextImage(it.result.toString())
                }
            } else {
                uploadImage(byteArray) {
                    //  dbManager.publishAd(ad!!, onPublishFinish())
                    nextImage(it.result.toString())
                }
            }
        } else {
            if (oldUrl.startsWith(HTTP)) {
                deleteImageByUrl(oldUrl) {
                    nextImage(EMPTY)
                }
            } else {
                nextImage(EMPTY)
            }
        }
    }

    private fun nextImage(uri: String) {
        setImageUriToAd(uri)
        imageIndex++
        uploadImages()
    }

    private fun setImageUriToAd(uri: String) {
        when (imageIndex) {
            0 -> ad = ad?.copy(mainImage = uri)
            1 -> ad = ad?.copy(image2 = uri)
            2 -> ad = ad?.copy(image3 = uri)
        }
    }

    private fun getUrlFromAd(): String {
        return listOf(ad?.mainImage!!, ad?.image2!!, ad?.image3!!)[imageIndex]
    }

    private fun prepareImageByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
        return outputStream.toByteArray()
    }

    private fun uploadImage(byteArray: ByteArray, listener: OnCompleteListener<Uri>) {
        val imStorageReference = dbManager.databaseStorage
            .child(dbManager.auth.uid!!).child("image_${System.currentTimeMillis()}")
        val uploadTask = imStorageReference.putBytes(byteArray)
        uploadTask.continueWithTask{
                task-> imStorageReference.downloadUrl
        }.addOnCompleteListener(listener)
    }

    private fun deleteImageByUrl(oldUrl: String, listener: OnCompleteListener<Void>) {
       dbManager.databaseStorage.storage.getReferenceFromUrl(oldUrl).delete()
           .addOnCompleteListener(listener)
    }

    private fun updateImage(byteArray: ByteArray, url: String, listener: OnCompleteListener<Uri>) {
        val imStorageReference = dbManager.databaseStorage.storage.getReferenceFromUrl(url)
        val uploadTask = imStorageReference.putBytes(byteArray)
        uploadTask.continueWithTask{
                task-> imStorageReference.downloadUrl
        }.addOnCompleteListener(listener)
    }

    private fun imageChangeCounter() {
        binding.vpImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateImageCounter(position)
            }
        })
    }

    private fun updateImageCounter(counter: Int) {
        var index = 1
        val itemCount = binding.vpImages.adapter?.itemCount
        if (itemCount == 0) index = 0
        val imageCounter = "${counter + index}/$itemCount"
        binding.tvImageCounter.text = imageCounter
    }

    companion object {
        private const val HTTP = "http"
        private const val EMPTY = "empty"
    }
}