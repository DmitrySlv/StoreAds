package com.ds_create.storeads.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ds_create.storeads.R
import com.ds_create.storeads.activities.EditAdsActivity

class RcViewDialogSpinnerAdapter(
   private val context: Context,
   private val dialog: AlertDialog
): RecyclerView.Adapter<RcViewDialogSpinnerAdapter.SpViewHolder>() {

   private val mainList = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sp_list_item, parent, false)
       return SpViewHolder(view, context, dialog)
    }

    override fun onBindViewHolder(holder: SpViewHolder, position: Int) {
        holder.setData(mainList[position])
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    class SpViewHolder(
        itemView: View, private val context: Context, private val dialog: AlertDialog
    ): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var itemtext = ""

        fun setData(text: String) {
            val tvSpItem = itemView.findViewById<TextView>(R.id.tvSpItem)
            tvSpItem.text = text
            itemtext = text
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            (context as EditAdsActivity).binding.tvCountry.text = itemtext
            dialog.dismiss()
        }
    }

    fun updateAdapter(list: ArrayList<String>) {
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }
}