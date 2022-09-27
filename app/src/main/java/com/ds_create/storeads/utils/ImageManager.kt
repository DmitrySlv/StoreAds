package com.ds_create.storeads.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.exifinterface.media.ExifInterface
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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

    fun chooseScaleType(imageView: ImageView, bitmap: Bitmap) {
        if (bitmap.width > bitmap.height) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

   suspend fun imageResize(uris: List<String>): List<Bitmap> = withContext(Dispatchers.IO) {
        val tempList = ArrayList<List<Int>>()
        val bitmapList = ArrayList<Bitmap>()
        for (n in uris.indices) {

            val size = getImageSize(uris[n])
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
        }
       for (i in uris.indices) {
        val e = kotlin.runCatching {
               bitmapList.add(
                   Picasso.get().load(File(uris[i]))
                       .resize(
                           tempList[i][WIDTH_IMAGE],
                           tempList[i][HEIGHT_IMAGE]
                       ).get()
               )
           }
           Log.d("MyLog", "Bitmap load done: ${e.isSuccess}")
       }
       return@withContext bitmapList
    }

   private const val MAX_IMAGE_SIZE = 1000
   private const val WIDTH_IMAGE = 0
   private const val HEIGHT_IMAGE = 1
}