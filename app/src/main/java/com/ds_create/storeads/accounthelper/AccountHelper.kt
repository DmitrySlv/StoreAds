package com.ds_create.storeads.accounthelper

import android.widget.Toast
import com.ds_create.storeads.MainActivity
import com.ds_create.storeads.R
import com.ds_create.storeads.dialoghelper.GoogleAccConst
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class AccountHelper(private val act: MainActivity) {

    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task->
                if (task.isSuccessful) {
                    sendEmailVerification(task.result.user!!)
                    act.uiUpdate(task.result.user)
                } else {

                    Toast.makeText(act, act.resources.getString(R.string.sign_up_error),
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task->
                if (task.isSuccessful) {
                    act.uiUpdate(task.result.user)
                } else {
                    Toast.makeText(act, act.resources.getString(R.string.sign_in_error),
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

    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.startActivityForResult(intent, GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id)).build()
        return GoogleSignIn.getClient(act, gso)
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(act, act.getString(R.string.sign_in_done), Toast.LENGTH_LONG).show()
            }
        }
    }
}