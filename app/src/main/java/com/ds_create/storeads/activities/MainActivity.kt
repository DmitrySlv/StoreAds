package com.ds_create.storeads.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.ds_create.storeads.R
import com.ds_create.storeads.databinding.ActivityMainBinding
import com.ds_create.storeads.utils.dialoghelper.DialogHelper
import com.ds_create.storeads.utils.dialoghelper.GoogleAccConst
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val dialogHelper = DialogHelper(this)
    val mAuth = FirebaseAuth.getInstance()
    private lateinit var tvAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_new_ads) {
            val intent = Intent(this, EditAdsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE) {
//            Log.d("MyLog", "Sign in result")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    Log.d("MyLog", "Api 0")
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken.toString())
                }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api error: ${e.message}")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun init() = with(binding) {
        setSupportActionBar(mainContent.toolbar)
        val toggle = ActionBarDrawerToggle(
            this@MainActivity,
           drawerLayout, mainContent.toolbar, R.string.open, R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this@MainActivity)
        tvAccount = navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       when (item.itemId) {
           R.id.my_ads -> {
               Toast.makeText(this, "Pressed id_my_ads", Toast.LENGTH_LONG).show()
           }
           R.id.id_car -> {
               Toast.makeText(this, "Pressed id_car", Toast.LENGTH_LONG).show()
           }
           R.id.id_pc -> {
               Toast.makeText(this, "Pressed id_pc", Toast.LENGTH_LONG).show()
           }
           R.id.sign_up -> {
               dialogHelper.createSignDialog(DialogHelper.SIGN_UP_STATE)
           }
           R.id.id_sign_in -> {
               dialogHelper.createSignDialog(DialogHelper.SIGN_IN_STATE)
           }
           R.id.id_sign_out -> {
               uiUpdate(null)
               mAuth.signOut()
               dialogHelper.accHelper.signOutGoogle()
           }
       }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?) {
        tvAccount.text = if (user == null) {
            resources.getString(R.string.not_reg)
        } else {
            user.email
        }
    }
}