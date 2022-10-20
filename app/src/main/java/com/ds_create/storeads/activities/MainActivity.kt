package com.ds_create.storeads.activities

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ds_create.storeads.R
import com.ds_create.storeads.adapters.AdsRcAdapter
import com.ds_create.storeads.databinding.ActivityMainBinding
import com.ds_create.storeads.models.AdModel
import com.ds_create.storeads.utils.accounthelper.AccountHelper
import com.ds_create.storeads.utils.dialoghelper.DialogHelper
import com.ds_create.storeads.utils.dialoghelper.GoogleAccConst
import com.ds_create.storeads.viewModel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
AdsRcAdapter.Listener {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val firebaseViewModel by lazy {
        ViewModelProvider(this)[FirebaseViewModel::class.java]
    }

    private val dialogHelper = DialogHelper(this)
    val mAuth = Firebase.auth
    private lateinit var tvAccount: TextView
    private lateinit var imAccount: ImageView
    private val adsRcAdapter = AdsRcAdapter(this)
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        initRcView()
        initViewModel()
        firebaseViewModel.loadAllAds()
        bottomMenuOnClick()
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    override fun onResume() {
        super.onResume()
        binding.mainContent.bNavView.selectedItemId = R.id.id_home
    }

    private fun onActivityResult() {
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
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
    }

    private fun init() = with(binding) {
        setSupportActionBar(mainContent.toolbar)
        onActivityResult()
        navViewSettings()
        val toggle = ActionBarDrawerToggle(
            this@MainActivity,
           drawerLayout, mainContent.toolbar, R.string.open, R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this@MainActivity)
        tvAccount = navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
        imAccount = navView.getHeaderView(0).findViewById(R.id.imAccountImage)
    }

    private fun initViewModel() {
        firebaseViewModel.liveAdsData.observe(this) {
            adsRcAdapter.updateAdapter(it)
            binding.mainContent.tvEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun initRcView() = with(binding) {
        mainContent.rcViewItems.layoutManager = LinearLayoutManager(this@MainActivity)
        mainContent.rcViewItems.adapter = adsRcAdapter
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       when (item.itemId) {
           R.id.my_ads -> {
               Toast.makeText(this, "Pressed id_my_ads", Toast.LENGTH_LONG).show()
           }
           R.id.my_favourite -> {
               Toast.makeText(this, "Pressed id_my_favourite", Toast.LENGTH_LONG).show()
           }
           R.id.id_car -> {
               Toast.makeText(this, "Pressed id_car", Toast.LENGTH_LONG).show()
           }
           R.id.id_pc -> {
               Toast.makeText(this, "Pressed id_pc", Toast.LENGTH_LONG).show()
           }
           R.id.id_smart -> {
               Toast.makeText(this, "Pressed id_smartphone", Toast.LENGTH_LONG).show()
           }
           R.id.id_dm -> {
               Toast.makeText(this, "Pressed id_dm", Toast.LENGTH_LONG).show()
           }
           R.id.sign_up -> {
               dialogHelper.createSignDialog(DialogHelper.SIGN_UP_STATE)
           }
           R.id.id_sign_in -> {
               dialogHelper.createSignDialog(DialogHelper.SIGN_IN_STATE)
           }
           R.id.id_sign_out -> {
               if (mAuth.currentUser?.isAnonymous == true) {
                   binding.drawerLayout.closeDrawer(GravityCompat.START)
                   return true
               }
               uiUpdate(null)
               mAuth.signOut()
               dialogHelper.accHelper.signOutGoogle()
           }
       }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?) {
        if (user == null) {
            dialogHelper.accHelper.signInAnonymously(object : AccountHelper.AccHelperListener {
                override fun onComplete() {
                    tvAccount.text = getString(R.string.guest)
                    imAccount.setImageResource(R.drawable.ic_account_def)
                }
            })
        } else if (user.isAnonymous) {
            tvAccount.text = getString(R.string.guest)
            imAccount.setImageResource(R.drawable.ic_account_def)
        } else if (!user.isAnonymous) {
            tvAccount.text = user.email
            Picasso.get().load(user.photoUrl).into(imAccount)
        }
    }

    private fun bottomMenuOnClick() = with(binding) {
        mainContent.bNavView.setOnNavigationItemSelectedListener { item->
            when (item.itemId) {
                R.id.id_new_ads -> {
                    val intent = Intent(this@MainActivity, EditAdsActivity::class.java)
                    startActivity(intent)
                }
                R.id.id_my_ads -> {
                    firebaseViewModel.loadMyAds()
                    mainContent.toolbar.title = getString(R.string.my_ads_title)
                }
                R.id.id_favs -> {
                    firebaseViewModel.loadMyFavourites()
                }
                R.id.id_home -> {
                   firebaseViewModel.loadAllAds()
                    mainContent.toolbar.title = getString(R.string.def_title)
                }
            }
            true
        }
    }

    override fun onDeleteItem(ad: AdModel) {
        firebaseViewModel.deleteItem(ad)
    }

    override fun onAdViewed(ad: AdModel) {
        firebaseViewModel.adViewed(ad)
        val i = Intent(this, DescriptionActivity::class.java)
        i.putExtra(DescriptionActivity.AD_NODE, ad)
        startActivity(i)
    }

    override fun onFavouriteClicked(ad: AdModel) {
        firebaseViewModel.onFavouritesClick(ad)
    }

    private fun navViewSettings() = with(binding) {
        val menu = navView.menu
        val adsCat = menu.findItem(R.id.ads_cat)
        val spanAdsCat = SpannableString(adsCat.title)
        adsCat.title?.let {
            spanAdsCat.setSpan(ForegroundColorSpan(
                ContextCompat.getColor(this@MainActivity, R.color.color_red)),
                0,
                it.length,
                0
            )
            adsCat.title = spanAdsCat
        }
        val adsAcc = menu.findItem(R.id.ads_acc)
        val spanAdsAcc = SpannableString(adsAcc.title)
        adsAcc.title?.let {
            spanAdsAcc.setSpan(ForegroundColorSpan(
                ContextCompat.getColor(this@MainActivity, R.color.color_red)),
                0,
                it.length,
                0
            )
            adsAcc.title = spanAdsAcc
        }
    }

    companion object {
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
    }
}