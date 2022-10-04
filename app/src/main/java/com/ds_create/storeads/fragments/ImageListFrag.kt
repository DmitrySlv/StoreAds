package com.ds_create.storeads.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ds_create.storeads.R
import com.ds_create.storeads.activities.EditAdsActivity
import com.ds_create.storeads.adapters.AdapterCallback
import com.ds_create.storeads.adapters.SelectImageRvAdapter
import com.ds_create.storeads.databinding.ListImageFragmentBinding
import com.ds_create.storeads.utils.ImageManager
import com.ds_create.storeads.utils.ImagePicker
import com.ds_create.storeads.utils.ItemTouchMoveCallback
import com.ds_create.storeads.utils.dialoghelper.ProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFrag(
    private val fragCloseInterface: FragmentCloseInterface,
    private val newList: ArrayList<String>?
    ): BaseAdsFrag(), AdapterCallback {

    private val adapter = SelectImageRvAdapter(this)
    private val dragCallback = ItemTouchMoveCallback(adapter)
    private val touchHelper = ItemTouchHelper(dragCallback)
    private var job: Job? = null
    private var addImageItem: MenuItem? = null

    private var _binding: ListImageFragmentBinding? = null
    private val binding: ListImageFragmentBinding
        get() = _binding ?: throw RuntimeException("ListImageFragmentBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListImageFragmentBinding.inflate(layoutInflater)
        adView = binding.adView
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
        if (newList != null) {
            resizeSelectedImages(newList, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose(adapter.mainArray)
        job?.cancel()
    }

    override fun onClose() {
        super.onClose()
        requireActivity().supportFragmentManager.beginTransaction()
            .remove(this@ImageListFrag)
            .commit()
    }

    override fun onItemDelete() {
        addImageItem?.isVisible = true
    }

    private fun resizeSelectedImages(newList: ArrayList<String>, needClear: Boolean) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog =  ProgressDialog.createProgressDialog(requireActivity())
            val bitmapList = ImageManager.imageResize(newList)
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)
            if (adapter.mainArray.size > 2) {
                addImageItem?.isVisible = false
            }
        }
    }

    private fun setUpToolBar() = with(binding) {
        toolbar.inflateMenu(R.menu.menu_choose_image)
        val deleteItem = toolbar.menu.findItem(R.id.id_delete_image)
        addImageItem = toolbar.menu.findItem(R.id.id_add_image)

        toolbar.setNavigationOnClickListener {
            showInterAd()
        }

        deleteItem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(), true)
            addImageItem?.isVisible = true
            true
        }

        addImageItem?.setOnMenuItemClickListener {
            val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
            ImagePicker.launcher(
                activity as EditAdsActivity,
                (activity as EditAdsActivity).launcherMultiSelectImages,
                imageCount
            )
            true
        }
    }

    fun updateAdapter(newList: ArrayList<String>) {
        resizeSelectedImages(newList, false)
    }

    fun updateAdapterFromEdit(bitmapList: List<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

    fun setSingleImage(uri: String, position: Int) {
        val pBar = binding.rcViewSelectImage[position].findViewById<ProgressBar>(R.id.pBar)
        job = CoroutineScope(Dispatchers.Main).launch {
            pBar.visibility = View.VISIBLE
            val bitmapList = ImageManager.imageResize(listOf(uri))
            pBar.visibility = View.GONE
            adapter.mainArray[position] = bitmapList[0]
            adapter.notifyItemChanged(position)
        }
    }
}