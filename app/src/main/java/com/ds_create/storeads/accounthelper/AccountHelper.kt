package com.ds_create.storeads.accounthelper

import android.widget.Toast
import com.ds_create.storeads.MainActivity
import com.ds_create.storeads.R
import com.google.firebase.auth.FirebaseUser

class AccountHelper(private val act: MainActivity) {

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task->
                if (task.isSuccessful) {
                    sendEmailVerification(task.result.user!!)
                } else {
                    Toast.makeText(act, act.resources.getString(R.string.sign_up_error),
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener {task->
            if (task.isSuccessful) {
                Toast.makeText(act, act.resources.getString(R.string.send_verification_done),
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(act, act.resources.getString(R.string.send_verification_email_error),
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}