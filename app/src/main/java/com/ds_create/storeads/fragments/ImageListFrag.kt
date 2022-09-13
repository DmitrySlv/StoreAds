package com.ds_create.storeads.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ds_create.storeads.R

class ImageListFrag(private val fragCloseInterface: FragmentCloseInterface): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_mage_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bBack = view.findViewById<Button>(R.id.bBack)
        bBack.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.remove(this@ImageListFrag)
                ?.commit()
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose()
    }
}