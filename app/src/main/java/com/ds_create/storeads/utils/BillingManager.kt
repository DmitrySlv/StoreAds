package com.ds_create.storeads.utils

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.ds_create.storeads.R

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
                    list?.get(0)?.let { nonConsumableItem(it) }
                }
            }
        }
    }

    private fun getItem() {
        val skuList = ArrayList<String>()
        skuList.add(REMOVE_ADS)
        val skuDetails = SkuDetailsParams.newBuilder()
        skuDetails.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient?.querySkuDetailsAsync(skuDetails.build()) { result, list ->
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

    private fun nonConsumableItem(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val accParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken).build()
                billingClient?.acknowledgePurchase(accParams) { result->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        savePurchase(true)
                        Toast.makeText(act, act.getString(R.string.thanks_for_purchase),
                            Toast.LENGTH_LONG).show()
                    } else {
                        savePurchase(false)
                        Toast.makeText(act, act.getString(R.string.not_released_purchase),
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun savePurchase(isPurchased: Boolean) {
        val pref = act.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(REMOVE_ADS_PREF, isPurchased)
        editor.apply()
    }

    fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {

            override fun onBillingServiceDisconnected() {}

            override fun onBillingSetupFinished(p0: BillingResult) {
                getItem()
            }
        })
    }

    fun closeConnection() {
        billingClient?.endConnection()
    }

    companion object {
        const val REMOVE_ADS = "remove_ads"
        const val REMOVE_ADS_PREF = "remove_ads_pref"
        const val MAIN_PREF = "main_pref"
    }
}