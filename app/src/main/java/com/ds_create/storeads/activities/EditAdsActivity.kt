package com.ds_create.storeads.activities

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        checkBoxWithSend.isChecked = ad.withSend.toBoolean()
        tvCat.text = ad.category
        edTitle.setText(ad.title)
        edPrice.setText(ad.price)
        edDescription.setText(ad.description)
        ImageManager.fillImageArray(ad, imageAdapter)
    }

    //OnClicks
    fun onClickSelectCountry(view: View) {
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listCountry, binding.tvCountry)
        if (binding.tvCity.text.toString() != getString(R.string.select_city)) {
            binding.tvCity.text = getString(R.string.select_city)
        }
    }

    fun onClickSelectCity(view: View) {
        val selectedCountry = binding.tvCountry.text.toString()
        if (selectedCountry != getString(R.string.select_country)) {
            val listCity = CityHelper.getAllCities(this, selectedCountry)
            dialog.showSpinnerDialog(this, listCity, binding.tvCity)
        } else {
            Toast.makeText(this,
                getString(R.string.no_country_selected), Toast.LENGTH_LONG).show()
        }
    }

    fun onClickSelectCat(view: View) {
        val listCategory = resources.getStringArray(R.array.category).toMutableList() as ArrayList
        dialog.showSpinnerDialog(this, listCategory, binding.tvCat)
    }

    fun onClickGetImages(view: View) {
        if (imageAdapter.mainArray.size == 0) {
        ImagePicker.getMultiImages(this, ImagePicker.MULTI_IMAGE_COUNTER)
    } else {
        openChooseImageFrag(null)
            chooseImageFrag?.updateAdapterFromEdit(imageAdapter.mainArray)
        }
    }

    fun onClickPublish(view: View) {
        ad = fillAd()
        if (isEditState){
            ad?.copy(key = ad?.key)?.let { dbManager.publishAd(it, onPublishFinish()) }
        } else {
           // dbManager.publishAd(adTemp, onPublishFinish())
            uploadImages()
        }
    }

    private fun onPublishFinish(): DbManager.FinishWorkListener {
        return object : DbManager.FinishWorkListener {
            override fun onFinishWork() {
                finish()
            }
        }
    }

    private fun fillAd(): AdModel {
        val ad: AdModel
        binding.apply {
            ad = AdModel(
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
                "empty",
                "empty",
                "empty",
                dbManager.database.push().key,
                dbManager.auth.uid,
                "0"
            )
        }
        return ad
    }

    override fun onFragClose(list: ArrayList<Bitmap>) {
        binding.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageFrag = null
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
        if (imageAdapter.mainArray.size == imageIndex) {
            dbManager.publishAd(ad!!, onPublishFinish())
            return
        }
        val byteArray = prepareImageByteArray(imageAdapter.mainArray[imageIndex])
        uploadImage(byteArray) {
          //  dbManager.publishAd(ad!!, onPublishFinish())
            nextImage(it.result.toString())
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
        }.addOnCompleteListener (listener)
    }
}