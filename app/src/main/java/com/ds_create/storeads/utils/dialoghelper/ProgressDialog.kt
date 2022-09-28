package com.ds_create.storeads.utils.dialoghelper

import android.app.Activity
import android.app.AlertDialog
import com.ds_create.storeads.databinding.ProgressDialogLayoutBinding

object ProgressDialog {

    fun createProgressDialog(act: Activity): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(act)
        val rootDialogElement = ProgressDialogLayoutBinding.inflate(act.layoutInflater)
        dialogBuilder.setView(rootDialogElement.root)

        val dialog = dialogBuilder.create()
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }
}