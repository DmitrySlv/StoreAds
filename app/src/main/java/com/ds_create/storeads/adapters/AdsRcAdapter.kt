package com.ds_create.storeads.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ds_create.storeads.R
import com.ds_create.storeads.activities.DescriptionActivity
import com.ds_create.storeads.activities.EditAdsActivity
import com.ds_create.storeads.activities.MainActivity
import com.ds_create.storeads.databinding.AdListItemBinding
import com.ds_create.storeads.models.AdModel
import com.ds_create.storeads.utils.ImageManager
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdsRcAdapter(private val activity: MainActivity): RecyclerView.Adapter<AdsRcAdapter.AdHolder>() {

    private val adsArray = ArrayList<AdModel>()
    private var timeFormatter: SimpleDateFormat? = null

    init {
        timeFormatter = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val binding = AdListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return AdHolder(binding, activity, timeFormatter!!)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adsArray[position])
    }

    override fun getItemCount(): Int {
       return adsArray.size
    }

    fun updateAdapter(newList: List<AdModel>) {
        val tempArray = ArrayList<AdModel>()
        tempArray.addAll(adsArray)
        tempArray.addAll(newList)

        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(adsArray, tempArray))
        diffResult.dispatchUpdatesTo(this)
        adsArray.clear()
        adsArray.addAll(tempArray)
    }

    fun updateAdapterWithClear(newList: List<AdModel>) {

        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(adsArray, newList))
        diffResult.dispatchUpdatesTo(this)
        adsArray.clear()
        adsArray.addAll(newList)
    }

    class AdHolder(
        private val binding: AdListItemBinding,
        private val activity: MainActivity,
        private val formatter: SimpleDateFormat
        ): ViewHolder(binding.root) {

        fun setData(ad: AdModel) = with(binding) {
            tvDescription.text = ad.description
            tvPrice.text = ad.price
            tvTitle.text = ad.title
            tvViewCounter.text = ad.viewsCounter
            tvFavCounter.text = ad.favouriteCounter
            val publishTime = activity.getString(R.string.publish_time) +
                    getTimeFromMillis(ad.time)
            tvPublishTime.text = publishTime
            Picasso.get().load(ad.mainImage).into(mainImage)

            isFavourite(ad)
            showEditPanel(isOwner(ad))
            mainOnClicks(ad)
        }

        private fun getTimeFromMillis(timeMillis: String): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeMillis.toLong()
            return formatter.format(calendar.time)
        }

        private fun mainOnClicks(ad: AdModel) = with(binding) {
            itemView.setOnClickListener {
                activity.onAdViewed(ad)
            }
            ibFav.setOnClickListener {
                if (activity.mAuth.currentUser?.isAnonymous == false) {
                    activity.onFavouriteClicked(ad)
                }
            }
            ibEditAd.setOnClickListener(onClickEdit(ad))
            ibDeleteAd.setOnClickListener {
                activity.onDeleteItem(ad)
            }
        }

        private fun isFavourite(ad: AdModel) = with(binding) {
            if (ad.isFavourite) {
                ibFav.setImageResource(R.drawable.ic_fav_pressed)
            } else {
                ibFav.setImageResource(R.drawable.ic_fav_normal)
            }
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

    interface Listener {
        fun onDeleteItem(ad: AdModel)
        fun onAdViewed(ad: AdModel)
        fun onFavouriteClicked(ad: AdModel)
    }

    companion object {
        private const val TIME_FORMAT = "dd/MM/yyyy - hh:mm"
    }
}