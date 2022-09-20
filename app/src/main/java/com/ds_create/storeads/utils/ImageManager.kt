package com.ds_create.storeads.utils

import android.graphics.BitmapFactory
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.File

object ImageManager {

    fun getImageSize(uri: String): List<Int> {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(uri, options)
        return if (imageRotation(uri) == 90) {
            listOf(options.outHeight, options.outWidth)
        } else {
            listOf(options.outWidth, options.outHeight)
        }
    }

   private fun imageRotation(uri: String): Int {
       val rotation: Int
       val imageFile = File(uri)
       val exif = ExifInterface(imageFile.absolutePath)
       val orientation = exif.getAttributeInt(
           ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
       )
       rotation = if (orientation == ExifInterface.ORIENTATION_ROTATE_90 ||
           orientation == ExifInterface.ORIENTATION_ROTATE_270
       ) {
           90
       } else {
           0
       }
       return rotation
   }

    fun imageResize(uris: List<String>) {
        val tempList = ArrayList<List<Int>>()
        for (n in uris.indices) {
            val size = getImageSize(uris[n])
            Log.d("MyLog", "Width: ${size[WIDTH_IMAGE]} Height: ${size[HEIGHT_IMAGE]}")

            val imageRatio = size[WIDTH_IMAGE].toFloat() / size[HEIGHT_IMAGE].toFloat()
            if (imageRatio > 1) {
                if (size[WIDTH_IMAGE] > MAX_IMAGE_SIZE) {
                    tempList.add(listOf(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE / imageRatio).toInt()))
                } else {
                    tempList.add(listOf(size[WIDTH_IMAGE], size[HEIGHT_IMAGE]))
                }
            } else {
                if (size[HEIGHT_IMAGE] > MAX_IMAGE_SIZE) {
                    tempList.add(listOf((MAX_IMAGE_SIZE * imageRatio).toInt(), MAX_IMAGE_SIZE))
                } else {
                    tempList.add(listOf(size[WIDTH_IMAGE], size[HEIGHT_IMAGE]))
                }
            }
            Log.d("MyLog", "Width: ${tempList[n][WIDTH_IMAGE]} Height: ${tempList[n][HEIGHT_IMAGE]}")
        }
    }

   private const val MAX_IMAGE_SIZE = 1000
   private const val WIDTH_IMAGE = 0
   private const val HEIGHT_IMAGE = 1
}