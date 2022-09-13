package com.ds_create.storeads.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ds_create.storeads.R
import com.ds_create.storeads.databinding.ActivityEditAdsBinding
import com.ds_create.storeads.dialogs.DialogSpinnerHelper
import com.ds_create.storeads.fragments.FragmentCloseInterface
import com.ds_create.storeads.fragments.ImageListFrag
import com.ds_create.storeads.utils.CityHelper
import com.ds_create.storeads.utils.ImagePicker
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil

class EditAdsActivity : AppCompatActivity(), FragmentCloseInterface {

    val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_GET_IMAGES) { //заменить RequestCode на свою константу
            if (data != null) {
                val returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                Log.d("MyLog", "Image :${returnValue?.get(0)}")
                Log.d("MyLog", "Image :${returnValue?.get(1)}")
                Log.d("MyLog", "Image :${returnValue?.get(2)}")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.getImages(this, 3)
                } else {
                    Toast.makeText(this,
                        "Approve permissions to open Pix ImagePicker",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    private fun init() {
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

    fun onClickGetImages(view: View) {
        binding.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.placeHolder, ImageListFrag(this))
        fm.commit()
    }

    override fun onFragClose() {
        binding.scrollViewMain.visibility = View.VISIBLE
    }
}