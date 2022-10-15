package com.ds_create.storeads.utils

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ds_create.storeads.R
import com.ds_create.storeads.activities.EditAdsActivity
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {

    const val MAX_IMAGE_COUNT = 3
    const val SINGLE_IMAGE_COUNTER = 1
    const val MULTI_IMAGE_COUNTER = 3
    const val OPTIONS = "options"
    private const val PATH_IMAGE_PICKER = "/pix/images"

    private fun getOptions(imageCounter: Int): Options {
        val options = Options().apply {
            count = imageCounter
            isFrontFacing = false
            mode = Mode.Picture
            path = PATH_IMAGE_PICKER
        }
        return options
    }

    fun getMultiImages(edAct: EditAdsActivity, imageCounter: Int) {
        edAct.addPixToActivity(R.id.placeHolder, getOptions(imageCounter)) { result->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    getMultiSelectImages(edAct, result.data)
                }
               PixEventCallback.Status.BACK_PRESSED -> {}
            }
        }
    }

    fun addImages(edAct: EditAdsActivity, imageCounter: Int) {
        val fragOld = edAct.chooseImageFrag
        edAct.addPixToActivity(R.id.placeHolder, getOptions(imageCounter)) { result->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    edAct.chooseImageFrag = fragOld
                    openChooseImageFrag(edAct, fragOld!!)
                        edAct.chooseImageFrag?.updateAdapter(result.data as ArrayList<Uri>, edAct)
                }
                PixEventCallback.Status.BACK_PRESSED -> {}
            }
        }
    }

    fun getSingleImage(edAct: EditAdsActivity) {
        edAct.addPixToActivity(R.id.placeHolder, getOptions(SINGLE_IMAGE_COUNTER)) { result->
            val fragOld = edAct.chooseImageFrag
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    edAct.chooseImageFrag = fragOld
                    openChooseImageFrag(edAct, fragOld!!)
                    singleImage(edAct, result.data[0])
                }
                PixEventCallback.Status.BACK_PRESSED -> {}
            }
        }
    }

    private fun openChooseImageFrag(edAct: EditAdsActivity, frag: Fragment) {
        edAct.supportFragmentManager.beginTransaction()
            .replace(R.id.placeHolder, frag).commit()
    }

    private fun closePixFragment(edAct: EditAdsActivity) {
        val fList = edAct.supportFragmentManager.fragments
        fList.forEach {
            if (it.isVisible) edAct.supportFragmentManager.beginTransaction()
                .remove(it).commit()
        }
    }

    fun getMultiSelectImages(edAct: EditAdsActivity, uris: List<Uri>) {
        if (uris.size > 1 && edAct.chooseImageFrag == null) {
            edAct.openChooseImageFrag(uris as ArrayList<Uri>)
        } else if (uris.size == 1 && edAct.chooseImageFrag == null) {
            CoroutineScope(Dispatchers.Main).launch {
                edAct.binding.pBarLoad.visibility = View.VISIBLE
                val bitmapArray =
                    ImageManager.imageResize(uris, edAct) as ArrayList<Bitmap>
                edAct.binding.pBarLoad.visibility = View.GONE
                edAct.imageAdapter.update(bitmapArray)
                closePixFragment(edAct)
            }
        }
    }

   private fun singleImage(edAct: EditAdsActivity, uri: Uri) {
       edAct.chooseImageFrag?.setSingleImage(uri, edAct.editImagePos)
   }
}