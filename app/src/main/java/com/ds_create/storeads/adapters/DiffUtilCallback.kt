package com.ds_create.storeads.adapters

import androidx.recyclerview.widget.DiffUtil
import com.ds_create.storeads.models.AdModel

class DiffUtilCallback(
    private val oldList: List<AdModel>,
    private val newList: List<AdModel>,
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
       return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].key == newList[newItemPosition].key
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}