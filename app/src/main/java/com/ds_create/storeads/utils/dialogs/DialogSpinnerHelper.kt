package com.ds_create.storeads.utils.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ds_create.storeads.R
import com.ds_create.storeads.adapters.RcViewDialogSpinnerAdapter
import com.ds_create.storeads.utils.CityHelper

class DialogSpinnerHelper {

    fun showSpinnerDialog(context: Context, list: ArrayList<String>, tvSelection: TextView) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        val rootView = LayoutInflater.from(context).inflate(R.layout.spinner_layout, null)
        val adapter = RcViewDialogSpinnerAdapter(tvSelection, dialog)
        val rcView = rootView.findViewById<RecyclerView>(R.id.rcSpView)
        val searchView = rootView.findViewById<SearchView>(R.id.svSpinner)
        rcView.adapter = adapter
        dialog.setView(rootView)
        adapter.updateAdapter(list)
        setSearchView(adapter, list, searchView, context)
        dialog.show()
    }

    private fun setSearchView(
        adapter: RcViewDialogSpinnerAdapter,
        list: ArrayList<String>, searchView: SearchView?,
        context: Context) {
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
               return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val tempList = CityHelper.filterListData(list, newText, context)
                adapter.updateAdapter(tempList)
                return true
            }
        })
    }
}