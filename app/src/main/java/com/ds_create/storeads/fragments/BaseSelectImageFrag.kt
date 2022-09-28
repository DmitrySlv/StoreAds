package com.ds_create.storeads.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ds_create.storeads.databinding.ListImageFragmentBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

open class BaseSelectImageFrag: Fragment() {

    private var _binding: ListImageFragmentBinding? = null
    val binding: ListImageFragmentBinding
        get() = _binding ?: throw RuntimeException("ListImageFragmentBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListImageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAds()
    }

    override fun onPause() {
        super.onPause()
        binding.adView.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.adView.destroy()
        _binding = null
    }

    private fun initAds() {
        MobileAds.initialize(activity as Activity)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }
}