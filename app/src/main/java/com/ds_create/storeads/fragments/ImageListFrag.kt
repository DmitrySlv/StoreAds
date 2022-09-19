package com.ds_create.storeads.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ds_create.storeads.R
import com.ds_create.storeads.adapters.SelectImageRvAdapter
import com.ds_create.storeads.databinding.ListImageFragmentBinding
import com.ds_create.storeads.utils.ImagePicker
import com.ds_create.storeads.utils.ItemTouchMoveCallback

class ImageListFrag(
    private val fragCloseInterface: FragmentCloseInterface,
    private val newList: ArrayList<String>
    ): Fragment() {

    private val adapter = SelectImageRvAdapter()
    private val dragCallback = ItemTouchMoveCallback(adapter)
    private val touchHelper = ItemTouchHelper(dragCallback)

    private var _binding: ListImageFragmentBinding? = null
    private val binding: ListImageFragmentBinding
        get() = _binding ?: throw RuntimeException("ListImageFragmentBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListImageFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        with(binding) {
            touchHelper.attachToRecyclerView(rcViewSelectImage)
            rcViewSelectImage.layoutManager = LinearLayoutManager(activity)
            rcViewSelectImage.adapter = adapter
            }
            adapter.updateAdapter(newList, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose(adapter.mainArray)
    }

    private fun setUpToolBar() {
        binding.toolbar.inflateMenu(R.menu.menu_choose_image)
        val deleteItem = binding.toolbar.menu.findItem(R.id.id_delete_image)
        val addImageItem = binding.toolbar.menu.findItem(R.id.id_add_image)

        binding.toolbar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.remove(this@ImageListFrag)
                ?.commit()
        }
        deleteItem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(), true)
            true
        }
        addImageItem.setOnMenuItemClickListener {
            val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
            ImagePicker.getImages(activity as AppCompatActivity, imageCount)
            true
        }
    }

    fun updateAdapter(newList: ArrayList<String>) {

        adapter.updateAdapter(newList, false)
    }
}