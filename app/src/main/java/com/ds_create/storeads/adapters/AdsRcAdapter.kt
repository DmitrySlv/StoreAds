package com.ds_create.storeads.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ds_create.storeads.activities.EditAdsActivity
import com.ds_create.storeads.activities.MainActivity
import com.ds_create.storeads.databinding.AdListItemBinding
import com.ds_create.storeads.models.AdModel
import com.google.firebase.auth.FirebaseAuth

class AdsRcAdapter(private val activity: MainActivity): RecyclerView.Adapter<AdsRcAdapter.AdsHolder>() {

    private val adsArray = ArrayList<AdModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsHolder {
        val binding = AdListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return AdsHolder(binding, activity)
    }

    override fun onBindViewHolder(holder: AdsHolder, position: Int) {
        holder.setData(adsArray[position])
    }

    override fun getItemCount(): Int {
       return adsArray.size
    }

    fun updateAdapter(newList: List<AdModel>) {
        adsArray.clear()
        adsArray.addAll(newList)
        notifyDataSetChanged()
    }

    class AdsHolder(
        private val binding: AdListItemBinding,
        private val activity: MainActivity
        ): ViewHolder(binding.root) {

        fun setData(ad: AdModel) = with(binding) {
            tvDescription.text = ad.description
            tvPrice.text = ad.price
            tvTitle.text = ad.title
            showEditPanel(isOwner(ad))

            ibEditAd.setOnClickListener(onClickEdit(ad))
        }

        private fun onClickEdit(ad: AdModel): OnClickListener {
            return OnClickListener {
                val edIntent = Intent(activity, EditAdsActivity::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE, true)
                    putExtra(MainActivity.ADS_DATA, ad)
                }
                activity.startActivity(edIntent)
            }
        }

        private fun isOwner(ad: AdModel): Boolean {
            return ad.uid == activity.mAuth.uid
        }

        private fun showEditPanel(isOwner: Boolean) {
            if (isOwner) {
                binding.editPanel.visibility = View.VISIBLE
            } else {
                binding.editPanel.visibility = View.GONE
            }
        }
    }
}