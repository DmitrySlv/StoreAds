package com.ds_create.storeads.utils

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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

    fun launcher(
        edAct: EditAdsActivity, launcher: ActivityResultLauncher<Intent>?, imageCounter: Int
    ) {
        edAct.addPixToActivity(R.id.placeHolder, getOptions(imageCounter)) { result->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                   val fList = edAct.supportFragmentManager.fragments
                    fList.forEach {
                        if (it.isVisible) edAct.supportFragmentManager.beginTransaction()
                            .remove(it).commit()
                    }
                }
               PixEventCallback.Status.BACK_PRESSED -> {
                   Toast.makeText(edAct, "BACK_PRESSED", Toast.LENGTH_SHORT).show()
               }
            }
        }
    }

    fun getLauncherForMultiSelectImages(edAct: EditAdsActivity): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
//            if (result.resultCode == AppCompatActivity.RESULT_OK) {
//                if (result.data != null) {
//                    val returnValues = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
//                    if (returnValues?.size!! > 1 && edAct.chooseImageFrag == null) {
//                        edAct.openChooseImageFrag(returnValues)
//                    } else if (returnValues.size == 1 && edAct.chooseImageFrag == null) {
//                        CoroutineScope(Dispatchers.Main).launch {
//                            edAct.binding.pBarLoad.visibility = View.VISIBLE
//                            val bitmapArray =
//                                ImageManager.imageResize(returnValues) as ArrayList<Bitmap>
//                            edAct.binding.pBarLoad.visibility = View.GONE
//                            edAct.imageAdapter.update(bitmapArray)
//                        }
//                    } else if (edAct.chooseImageFrag != null) {
//                        edAct.chooseImageFrag?.updateAdapter(returnValues)
//                    }
//                }
//            }
        }
    }

    fun getLauncherForSingleImage(edAct: EditAdsActivity): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
//            if (result.resultCode == AppCompatActivity.RESULT_OK
//            ) {
//                if (result.data != null) {
//                    val uris = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
//                    edAct.chooseImageFrag?.setSingleImage(uris?.get(0)!!, edAct.editImagePos)
//                }
//            }
        }
    }
}