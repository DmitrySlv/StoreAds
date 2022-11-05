package com.ds_create.storeads.utils.accounthelper

import android.util.Log
import android.widget.Toast
import com.ds_create.storeads.R
import com.ds_create.storeads.activities.MainActivity
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
            act.mAuth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    act.mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signUpWithEmailSuccessful(task.result.user!!)
                            } else {
                                signUpWithEmailExceptions(task.exception!!, email, password)
                            }
                        }
                }
            }
        }
    }

    private fun signUpWithEmailSuccessful(user: FirebaseUser) {
        sendEmailVerification(user)
        act.uiUpdate(user)
    }

    private fun signUpWithEmailExceptions(except: Exception, email: String, password: String) {
        // Log.d("MyLog", "Exception: ${except}")
        if (except is FirebaseAuthUserCollisionException) {
            if (except.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                //Link email
                linkEmailToGoogle(email, password)
            }
        }
        if (except is FirebaseAuthInvalidCredentialsException) {
            if (except.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(
                    act, FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_LONG
                ).show()
//                                Log.d("MyLog", "Exception: ${except.errorCode}")
            }
        }
        if (except is FirebaseAuthWeakPasswordException) {
            // Log.d("MyLog", "Exception: ${except.errorCode}")
            if (except.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                Toast.makeText(
                    act, FirebaseAuthConstants.ERROR_WEAK_PASSWORD,
                    Toast.LENGTH_LONG
                ).show()
//                                Log.d("MyLog", "Exception: ${except.errorCode}")
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.currentUser?.delete()?.addOnCompleteListener { task->
                if (task.isSuccessful) {
                    act.mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            act.uiUpdate(task.result.user)
                        } else {
                            signInWitEmailExceptions(task.exception!!, email, password)
                        }
                    }
                }
            }

        }
    }

    private fun signInWitEmailExceptions(except: Exception, email: String, password: String) {
//                    Log.d("MyLog", "Exception: ${except}")
        if (except is FirebaseAuthInvalidCredentialsException) {
//                        Log.d("MyLog", "Exception2: ${except.errorCode}")
            if (except.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(
                    act, FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_LONG
                ).show()
            }
            if (except.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                Toast.makeText(
                    act, FirebaseAuthConstants.ERROR_WRONG_PASSWORD,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        if (except is FirebaseAuthInvalidUserException) {
            if (except.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                Toast.makeText(
                    act, FirebaseAuthConstants.ERROR_USER_NOT_FOUND,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun linkEmailToGoogle(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (act.mAuth.currentUser != null) {
            act.mAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        act, act.resources.getString(R.string.link_done),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                act, act.resources.getString(R.string.enter_to_google),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    act, act.resources.getString(R.string.send_verification_done),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    act, act.resources.getString(R.string.send_verification_email_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.googleSignInLauncher.launch(intent)
    }

    fun signOutGoogle() {
        getSignInClient().signOut()
        Toast.makeText(act, act.resources.getString(R.string.exit_from_account), Toast.LENGTH_LONG)
            .show()
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id))
            .requestEmail().build()
        return GoogleSignIn.getClient(act, gso)
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.mAuth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                act.mAuth.signInWithCredential(credential).addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        act.uiUpdate(task2.result.user)
                        Toast.makeText(act, act.getString(R.string.sign_in_done), Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(act, act.getString(R.string.google_sign_in_exception) +
                                task2.exception, Toast.LENGTH_LONG).show()
                        Log.d("MyLog", "Google Sign In Exception: ${task2.exception}")
                    }
                }
            }
        }
    }

    fun signInAnonymously(accHelperListener: AccHelperListener) {
        act.mAuth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                accHelperListener.onComplete()
                Toast.makeText(act, act.getString(R.string.enter_guest_successfully),
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(act, act.getString(R.string.enter_guest_is_not_successfully),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface AccHelperListener {
        fun onComplete()
    }
}