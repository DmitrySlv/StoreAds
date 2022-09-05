package com.ds_create.storeads.dialoghelper

import android.app.AlertDialog
import com.ds_create.storeads.MainActivity
import com.ds_create.storeads.R
import com.ds_create.storeads.accounthelper.AccountHelper
import com.ds_create.storeads.databinding.SignDialogBinding

class DialogHelper(private val act: MainActivity) {

    private val accHelper = AccountHelper(act)

    fun createSignDialog(index: Int) {
        val dialogBuilder = AlertDialog.Builder(act)
        val rootDialogElement = SignDialogBinding.inflate(act.layoutInflater)
        if (index == SIGN_UP_STATE) {
            rootDialogElement.tvSignTitle.text = act.resources.getString(R.string.aс_sign_up)
            rootDialogElement.btSignUpIn.text = act.resources.getString(R.string.sign_up_action)
        } else {
            rootDialogElement.tvSignTitle.text = act.resources.getString(R.string.aс_sign_in)
            rootDialogElement.btSignUpIn.text = act.resources.getString(R.string.sign_in_action)
        }
        rootDialogElement.btSignUpIn.setOnClickListener {
            if (index == SIGN_UP_STATE) {
                accHelper.signUpWithEmail(
                    rootDialogElement.edSignEmail.text.toString(),
                    rootDialogElement.edSignPassword.text.toString()
                )
            } else {

            }
        }
        dialogBuilder.setView(rootDialogElement.root)
        dialogBuilder.show()
    }

    companion object {
        const val SIGN_UP_STATE = 0
        const val SIGN_IN_STATE = 1
    }
}