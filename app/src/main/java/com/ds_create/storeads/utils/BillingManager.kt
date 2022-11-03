package com.ds_create.storeads.utils

import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams

class BillingManager(private val act: AppCompatActivity) {
    private var billingClient: BillingClient? = null

    init {
        setUpBillingClient()
    }

    private fun setUpBillingClient() {
        billingClient = BillingClient.newBuilder(act).setListener(getPurchaseListener())
            .enablePendingPurchases().build()
    }

    private fun getPurchaseListener(): PurchasesUpdatedListener {
        return PurchasesUpdatedListener {
            result, list ->
            run {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    list?.get(0).let {}
                }
            }
        }
    }

    private fun getItem() {
        val skuList = ArrayList<String>()
        skuList.add(REMOVE_ADS)
        val skuDetails = SkuDetailsParams.newBuilder()
        skuDetails.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient?.querySkuDetailsAsync(skuDetails.build()) {
                result, list ->
            run {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (!list.isNullOrEmpty()) {
                        val billingFlowParams = BillingFlowParams
                            .newBuilder().setSkuDetails(list[0]).build()
                        billingClient?.launchBillingFlow(act, billingFlowParams)
                    }
                }
            }
        }

    }

    fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {

            override fun onBillingServiceDisconnected() {
                TODO("Not yet implemented")
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                getItem()
            }
        })
    }

    companion object {
       private const val REMOVE_ADS = "remove_ads"
    }
}