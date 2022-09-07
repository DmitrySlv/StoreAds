package com.ds_create.storeads.dialoghelper

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.ds_create.storeads.MainActivity
import com.ds_create.storeads.R
import com.ds_create.storeads.accounthelper.AccountHelper
import com.ds_create.storeads.databinding.SignDialogBinding

class DialogHelper(private val act: MainActivity) {

    val accHelper = AccountHelper(act)

    fun createSignDialog(index: Int) {
        val dialogBuilder = AlertDialog.Builder(act)
        val rootDialogElement = SignDialogBinding.inflate(act.layoutInflater)
        dialogBuilder.setView(rootDialogElement.root)
        setDialogState(index, rootDialogElement)

        val dialog = dialogBuilder.create()
        rootDialogElement.btSignUpIn.setOnClickListener {
            setOnClickSignUpIn(index, rootDialogElement, dialog)
        }
        rootDialogElement.btForgetP.setOnClickListener {
            setOnClickResetPassword(rootDialogElement, dialog)
        }
        rootDialogElement.btGoogleSignIn.setOnClickListener {
           accHelper.signInWithGoogle()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setOnClickResetPassword(rootDialogElement: SignDialogBinding, dialog: AlertDialog?) {
        if (rootDialogElement.edSignEmail.text.isNotEmpty()) {
            act.mAuth.sendPasswordResetEmail(rootDialogElement.edSignEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(act, R.string.email_reset_password_was_sent, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            dialog?.dismiss()
        } else {
            rootDialogElement.tvDialogMessage.visibility = View.VISIBLE
        }
    }

    private fun setOnClickSignUpIn(index: Int, rootDialogElement: SignDialogBinding, dialog: AlertDialog?) {
        dialog?.dismiss()
        if (index == SIGN_UP_STATE) {
            accHelper.signUpWithEmail(
                rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString()
            )
        } else {
            accHelper.signInWithEmail(
                rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString()
            )
        }
    }

    private fun setDialogState(index: Int, rootDialogElement: SignDialogBinding) {
        if (index == SIGN_UP_STATE) {
            rootDialogElement.tvSignTitle.text = act.resources.getString(R.string.aс_sign_up)
            rootDialogElement.btSignUpIn.text = act.resources.getString(R.string.sign_up_action)
        } else {
            rootDialogElement.tvSignTitle.text = act.resources.getString(R.string.aс_sign_in)
            rootDialogElement.btSignUpIn.text = act.resources.getString(R.string.sign_in_action)
            rootDialogElement.btForgetP.visibility = View.VISIBLE
        }
    }

    companion object {
        const val SIGN_UP_STATE = 0
        const val SIGN_IN_STATE = 1
    }
}