/*
 * Created by Arcturus Mengsk
 *   2021.
 */

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
}