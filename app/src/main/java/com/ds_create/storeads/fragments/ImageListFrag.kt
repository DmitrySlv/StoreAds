package com.ds_create.storeads.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ds_create.storeads.R
import com.ds_create.storeads.adapters.SelectImageItem
import com.ds_create.storeads.adapters.SelectImageRvAdapter
import com.ds_create.storeads.utils.ItemTouchMoveCallback

class ImageListFrag(
    private val fragCloseInterface: FragmentCloseInterface,
    private val newList: ArrayList<String>
    ): Fragment() {

    private val adapter = SelectImageRvAdapter()
    private val dragCallback = ItemTouchMoveCallback(adapter)
    private val touchHelper = ItemTouchHelper(dragCallback)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_image_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bBack = view.findViewById<Button>(R.id.bBack)
        val rcView = view.findViewById<RecyclerView>(R.id.rcViewSelectImage)
        touchHelper.attachToRecyclerView(rcView)
        rcView.layoutManager = LinearLayoutManager(activity)
        rcView.adapter = adapter
        val updateList = ArrayList<SelectImageItem>()
        for (n in 0 until newList.size) {
            updateList.add(SelectImageItem(n.toString(), newList[n]))
        }
        adapter.updateAdapter(updateList)
        bBack.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.remove(this@ImageListFrag)
                ?.commit()
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose(adapter.mainArray)
        Log.d("MyLog", "Title 0: ${adapter.mainArray[0].title}")
        Log.d("MyLog", "Title 1: ${adapter.mainArray[1].title}")
        Log.d("MyLog", "Title 2: ${adapter.mainArray[2].title}")
    }
}