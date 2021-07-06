package com.anothermovieapp.movieList

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.anothermovieapp.R
import com.anothermovieapp.common.Constants

class SortOrderPicker : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(R.string.ui_favourites_sort_order)
                .setItems(R.array.sort_orders_array) { dialog, which ->
                    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                    preferences.edit().putInt(Constants.SORT_ORDER, which).apply()
                    targetFragment!!.onActivityResult(
                            targetRequestCode, Activity.RESULT_OK,
                            activity!!.intent)
                }
        return builder.create()
    }
}