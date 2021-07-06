package com.anothermovieapp.common

import android.content.Context
import android.util.TypedValue
import android.widget.Toast

object GeneralUtils {
    private var toast_short: Toast? = null
    fun showToastMessage(context: Context?, msg: String?) {
        toast_short?.cancel()
        toast_short = Toast.makeText(context, msg, Toast.LENGTH_LONG)
        toast_short?.show()
    }

    fun dipToPixels(context: Context, dipValue: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
    }
}