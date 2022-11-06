package com.ds_create.storeads.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
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
import androidx.recyclerview.widget.RecyclerView
import com.ds_create.storeads.R
import com.ds_create.storeads.adapters.AdsRcAdapter
import com.ds_create.storeads.databinding.ActivityMainBinding
import com.ds_create.storeads.models.AdModel
import com.ds_create.storeads.utils.AppMainState
import com.ds_create.storeads.utils.BillingManager
import com.ds_create.storeads.utils.FilterManager
import com.ds_create.storeads.utils.accounthelper.AccountHelper
import com.ds_create.storeads.utils.dialoghelper.DialogHelper
import com.ds_create.storeads.viewModel.FirebaseViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import io.ak1.pix.helpers.toast

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
    lateinit var filterLauncher: ActivityResultLauncher<Intent>
    private var clearUpdate: Boolean = true
    private var currentCategory: String? = null
    private var filter: String = "empty"
    private var filterDbManager: String = ""
    private var pref: SharedPreferences? = null
    private var isPremiumUser = false
    private var billingManager: BillingManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        pref = getSharedPreferences(BillingManager.MAIN_PREF, MODE_PRIVATE)
        isPremiumUser = pref?.getBoolean(BillingManager.REMOVE_ADS_PREF, false)!!
        if(!isPremiumUser) {
            (application as AppMainState).showAdIfAvailable(this) {
                //Если реклама была перекрыта каким-либо другим диалог окном, запустить это диалогов окно тут
            }
            initAds()
        } else {
            binding.mainContent.adView2.visibility = View.GONE
        }
        init()
        initRcView()
        initViewModel()
        bottomMenuOnClick()
        scrollListener()
        onActivityResultFilter()
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    override fun onPause() {
        super.onPause()
        binding.mainContent.adView2.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.mainContent.bNavView.selectedItemId = R.id.id_home
        binding.mainContent.adView2.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mainContent.adView2.destroy()
        billingManager?.closeConnection()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_filter) {
            val intent = Intent(this@MainActivity, FilterActivity::class.java).apply {
                putExtra(FilterActivity.FILTER_KEY, filter)
            }
            filterLauncher.launch(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initAds() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.mainContent.adView2.loadAd(adRequest)
    }

    private fun onActivityResult() {
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    Log.d("MyLog", "Api 0")
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken.toString())
                }
            } catch (e: ApiException) {
                Toast.makeText(this, getString(R.string.main_act_exception) + e.message,
                    Toast.LENGTH_LONG).show()
                Log.d("MyLog", "Api error: ${e.message}")
            }
        }
    }

    private fun onActivityResultFilter() {
        filterLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                filter = it.data?.getStringExtra(FilterActivity.FILTER_KEY)!!
               // Log.d("MyLog", "Filter: $filter")
               // Log.d("MyLog", "getFilter: ${FilterManager.getFilter(filter)}")
                filterDbManager = FilterManager.getFilter(filter)
            } else if (it.resultCode == RESULT_CANCELED) {
                filter = "empty"
                filterDbManager = ""
            }
        }
    }


    private fun init() = with(binding) {
        currentCategory = getString(R.string.ad_def)
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
            val list = getAdsByCategory(it)
            if (!clearUpdate) {
                adsRcAdapter.updateAdapter(list)
            } else {
                adsRcAdapter.updateAdapterWithClear(list)
            }
            binding.mainContent.tvEmpty.visibility = if (adsRcAdapter.itemCount == 0) {
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
        clearUpdate = true
       when (item.itemId) {
           R.id.my_ads -> {
               Toast.makeText(this, "Pressed id_my_ads", Toast.LENGTH_LONG).show()
           }
           R.id.my_favourite -> {
               getAdsFromCat(getString(R.string.ad_my_favourite))
           }
           R.id.id_car -> {
               getAdsFromCat(getString(R.string.ad_car))
           }
           R.id.id_pc -> {
               getAdsFromCat(getString(R.string.ad_pc))
           }
           R.id.id_smart -> {
               getAdsFromCat(getString(R.string.ad_smartphone))
           }
           R.id.id_dm -> {
               getAdsFromCat(getString(R.string.ad_dm))
           }
           R.id.remove_ads -> {
               billingManager = BillingManager(this)
               billingManager?.startConnection()
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

    private fun getAdsByCategory(list: ArrayList<AdModel>): ArrayList<AdModel> {
        val tempList = ArrayList<AdModel>()
        tempList.addAll(list)
        if (currentCategory != getString(R.string.ad_def)) {
            tempList.clear()
            list.forEach {
                if (currentCategory == it.category) {
                    tempList.add(it)
                }
            }
        }
        tempList.reverse()
        return tempList
    }

    private fun getAdsFromCat(cat: String) {
        currentCategory = cat
        firebaseViewModel.loadAllAdsFromCat(cat, filterDbManager)
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
        mainContent.bNavView.setOnItemSelectedListener { item ->
            clearUpdate = true
            when (item.itemId) {
                R.id.id_new_ads -> {
                    if (mAuth.currentUser != null) {
                        if (!mAuth.currentUser?.isAnonymous!!) {
                            val intent = Intent(this@MainActivity, EditAdsActivity::class.java)
                            startActivity(intent)
                        } else {
                            showToast(getString(R.string.error_guest_publication))
                        }
                    } else {
                        showToast(getString(R.string.error_by_registration))
                    }
                }
                R.id.id_my_ads -> {
                    firebaseViewModel.loadMyAds()
                    mainContent.toolbar.title = getString(R.string.my_ads_title)
                }
                R.id.id_favs -> {
                    firebaseViewModel.loadMyFavourites()
                }
                R.id.id_home -> {
                    currentCategory = getString(R.string.ad_def)
                    firebaseViewModel.loadAllAdsFirstPage(filterDbManager)
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
                START_SPAN, it.length, 0)
            adsCat.title = spanAdsCat
        }

        val adsAcc = menu.findItem(R.id.ads_acc)
        val spanAdsAcc = SpannableString(adsAcc.title)
        adsAcc.title?.let {
            spanAdsAcc.setSpan(ForegroundColorSpan(
                ContextCompat.getColor(this@MainActivity, R.color.color_red)),
                START_SPAN, it.length, 0)
            adsAcc.title = spanAdsAcc
        }

        val removeAdsCat = menu.findItem(R.id.remove_ads)
        val spanRemoveAdsCat = SpannableString(removeAdsCat.title)
        removeAdsCat.title?.let {
            spanRemoveAdsCat.setSpan(ForegroundColorSpan(
                ContextCompat.getColor(this@MainActivity, R.color.green)),
                START_SPAN, it.length, 0)
            removeAdsCat.title = spanRemoveAdsCat
        }
    }

    private fun scrollListener() = with(binding.mainContent) {
        rcViewItems.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!rcViewItems.canScrollVertically(SCROLL_DOWN) &&
                    newState == RecyclerView.SCROLL_STATE_IDLE
                ) {
                    clearUpdate = false
                    val adsList = firebaseViewModel.liveAdsData.value!!
                    if (adsList.isNotEmpty()) {
                        getAdsFromCat(adsList)
                    }
                }
            }
        })
    }

    private fun getAdsFromCat(adsList: ArrayList<AdModel>) {
        adsList[0].let {
            if (currentCategory == getString(R.string.ad_def)) {
                firebaseViewModel.loadAllAdsNextPage(it.time, filterDbManager)
            } else {
                firebaseViewModel.loadAllAdsFromCatNextPage(it.category!!, it.time, filterDbManager)
            }
        }
    }

    companion object {
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
        private const val START_SPAN = 0
        private const val SCROLL_DOWN = 1
    }
}