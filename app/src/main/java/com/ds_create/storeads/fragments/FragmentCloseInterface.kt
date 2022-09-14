package com.ds_create.storeads.fragments

import com.ds_create.storeads.adapters.SelectImageItem

interface FragmentCloseInterface {
    fun onFragClose(list: ArrayList<SelectImageItem>)
}