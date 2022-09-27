package com.ds_create.storeads.utils

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ds_create.storeads.activities.EditAdsActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {

    const val REQUEST_CODE_GET_IMAGES = 999
    const val REQUEST_CODE_GET_SINGLE_IMAGE = 998
    const val MAX_IMAGE_COUNT = 3

    fun getImages(context: AppCompatActivity, imageCounter: Int, rCode: Int) {
        val options = Options.init()
            .setRequestCode(rCode)
            .setCount(imageCounter)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("/pix/images")

        Pix.start(context, options)
    }

    fun showSelectedImages(resultCode: Int, requestCode: Int, data: Intent?, edAct: EditAdsActivity) {
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE_GET_IMAGES) { //заменить RequestCode на свою константу

            if (data != null) {
                val returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)

                if (returnValues?.size!! > 1 && edAct.chooseImageFrag == null) {
                   edAct.openChooseImageFrag(returnValues)

                } else if (returnValues.size == 1 && edAct.chooseImageFrag == null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        edAct.binding.pBarLoad.visibility = View.VISIBLE
                        val bitmapArray = ImageManager.imageResize(returnValues) as ArrayList<Bitmap>
                        edAct.binding.pBarLoad.visibility = View.GONE
                        edAct.imageAdapter.update(bitmapArray)
                    }

                } else if (edAct.chooseImageFrag != null) {
                    edAct.chooseImageFrag?.updateAdapter(returnValues)
                }
            }
        } else if(resultCode == AppCompatActivity.RESULT_OK &&
            requestCode == REQUEST_CODE_GET_SINGLE_IMAGE) {
            if (data != null) {
                val uris = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                edAct.chooseImageFrag?.setSingleImage(uris?.get(0)!!, edAct.editImagePos)
            }
        }
    }
}