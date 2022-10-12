package com.ds_create.storeads.utils.accounthelper

import android.util.Log
import android.widget.Toast
import com.ds_create.storeads.activities.MainActivity
import com.ds_create.storeads.R
import com.ds_create.storeads.utils.constants.FirebaseAuthConstants
import com.ds_create.storeads.utils.dialoghelper.GoogleAccConst
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*

class AccountHelper(private val act: MainActivity) {

    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        sendEmailVerification(task.result.user!!)
                        act.uiUpdate(task.result.user)
                    } else {
                        Log.d("MyLog", "Exception: ${task.exception}")
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            val exception = task.exception as FirebaseAuthUserCollisionException
                            if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
//                                Toast.makeText(act, FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE,
//                                    Toast.LENGTH_LONG).show()
                                //Link email
                                linkEmailToGoogle(email, password)
                            }
                        }
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            val exception = task.exception as FirebaseAuthInvalidCredentialsException
                            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                                Toast.makeText(act, FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                                    Toast.LENGTH_LONG).show()
//                                Log.d("MyLog", "Exception: ${exception.errorCode}")
                            }
                        }
                        if (task.exception is FirebaseAuthWeakPasswordException) {
                            val exception = task.exception as FirebaseAuthWeakPasswordException
                            Log.d("MyLog", "Exception: ${exception.errorCode}")
                            if (exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                                Toast.makeText(act, FirebaseAuthConstants.ERROR_WEAK_PASSWORD,
                                    Toast.LENGTH_LONG).show()
//                                Log.d("MyLog", "Exception: ${exception.errorCode}")
                            }
                        }
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
//                    Log.d("MyLog", "Exception: ${task.exception}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        val exception = task.exception as FirebaseAuthInvalidCredentialsException
//                        Log.d("MyLog", "Exception2: ${exception.errorCode}")
                        if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                            Toast.makeText(act, FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                                Toast.LENGTH_LONG).show()
                        }
                        if (exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                            Toast.makeText(act, FirebaseAuthConstants.ERROR_WRONG_PASSWORD,
                                Toast.LENGTH_LONG).show()
                        }
                    }
                    if (task.exception is FirebaseAuthInvalidUserException) {
                        val exception = task.exception as FirebaseAuthInvalidUserException
                        if (exception.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND){
                            Toast.makeText(act, FirebaseAuthConstants.ERROR_USER_NOT_FOUND,
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun linkEmailToGoogle(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (act.mAuth.currentUser != null) {
            act.mAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(act, act.resources.getString(R.string.link_done),
                        Toast.LENGTH_LONG).show()
                }
            }
        } else {
                Toast.makeText(act, act.resources.getString(R.string.enter_to_google),
                    Toast.LENGTH_LONG).show()
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

    fun signOutGoogle() {
        getSignInClient().signOut()
        Toast.makeText(act, act.resources.getString(R.string.exit_from_account), Toast.LENGTH_LONG).show()
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id)).requestEmail().build()
        return GoogleSignIn.getClient(act, gso)
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.mAuth.currentUser?.delete()?.addOnCompleteListener { task->
            if (task.isSuccessful) {
                act.mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        act.uiUpdate(task.result.user)
                        Toast.makeText(act, act.getString(R.string.sign_in_done), Toast.LENGTH_LONG).show()
                    } else {
                        Log.d("MyLog", "Google Sign In Exception: ${task.exception}")
                    }
                }
            }
        }
    }

    fun signInAnonymously(accHelperListener: AccHelperListener) {
        act.mAuth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                accHelperListener.onComplete()
                Toast.makeText(act, "Вы вошли как гость", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(act, "Не удалось войти как гость", Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface AccHelperListener {
        fun onComplete()
    }
}