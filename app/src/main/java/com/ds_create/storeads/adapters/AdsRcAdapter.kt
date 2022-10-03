package com.ds_create.storeads.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ds_create.storeads.databinding.AdListItemBinding
import com.ds_create.storeads.models.AdModel

class AdsRcAdapter: RecyclerView.Adapter<AdsRcAdapter.AdsHolder>() {

    private val adsArray = ArrayList<AdModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context))
        return AdsHolder(binding)
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

    class AdsHolder(private val binding: AdListItemBinding): ViewHolder(binding.root) {

        fun setData(ad: AdModel)= with(binding) {
            tvDescription.text = ad.description
            tvPrice.text = ad.price
            tvPrice.text = ad.price
        }
    }
}