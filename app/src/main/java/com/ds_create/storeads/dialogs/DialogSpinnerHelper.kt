package com.ds_create.storeads.dialogs

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ds_create.storeads.R
import com.ds_create.storeads.utils.CityHelper

class DialogSpinnerHelper {

    fun showSpinnerDialog(context: Context, list: ArrayList<String>, application: Application) {
        val builder = AlertDialog.Builder(context)
        val rootView = LayoutInflater.from(context).inflate(R.layout.spinner_layout, null)
        val adapter = RcViewDialogSpinnerAdapter()
        val rcView = rootView.findViewById<RecyclerView>(R.id.rcSpView)
        val searchView = rootView.findViewById<SearchView>(R.id.svSpinner)
        rcView.layoutManager = LinearLayoutManager(context)
        rcView.adapter = adapter
        builder.setView(rootView)
        adapter.updateAdapter(list)
        setSearchView(adapter, list, searchView, application)
        builder.show()
    }

    private fun setSearchView(
        adapter: RcViewDialogSpinnerAdapter,
        list: ArrayList<String>, searchView: SearchView?, application: Application) {
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
               return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val tempList = CityHelper.filterListData(list, newText, application)
                adapter.updateAdapter(tempList)
                return true
            }
        })
    }
}