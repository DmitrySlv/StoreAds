package com.ds_create.storeads.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ds_create.storeads.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

open class BaseAdsFrag: Fragment(), InterAdsClose {

    lateinit var adView: AdView
    var interAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadInterAd()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAds()
    }

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }

    private fun initAds() {
        MobileAds.initialize(requireActivity())
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun loadInterAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireActivity(), getString(R.string.ad_inter_id), adRequest,
            object: InterstitialAdLoadCallback() {

                override fun onAdLoaded(ad: InterstitialAd) {
                    interAd = ad
                }
            }
        )
    }

    fun showInterAd() {
        if (interAd != null) {
            interAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

                override fun onAdDismissedFullScreenContent() {
                 onClose()
                }
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    onClose()
                }

            }
            //обязательно добавить для запуска рекламы
            interAd?.show(requireActivity())
        } else {
            onClose()
        }
    }

    override fun onClose() {}
}