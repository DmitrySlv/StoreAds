package com.ds_create.storeads.utils

import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix

object ImagePicker {

    const val REQUEST_CODE_GET_IMAGES = 999
    const val MAX_IMAGE_COUNT = 3

    fun getImages(context: AppCompatActivity, imageCounter: Int) {
        val options = Options.init()
            .setRequestCode(REQUEST_CODE_GET_IMAGES)
            .setCount(imageCounter)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("/pix/images")

        Pix.start(context, options)
    }
}