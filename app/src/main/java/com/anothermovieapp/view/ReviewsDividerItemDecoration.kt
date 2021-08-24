/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.anothermovieapp.R

class ReviewsDividerItemDecoration(context: Context) : ItemDecoration() {
    private val mDivider: Drawable? =
        ResourcesCompat.getDrawable(context.resources, R.drawable.shadowline, null)

    override fun onDrawOver(
        c: Canvas, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            if (parent.getChildAt(i + 1) == null) {
                //do not draw if it is last item
                continue
            }
            val params = view.layoutParams as RecyclerView.LayoutParams
            val reviewText = view.findViewById<View>(R.id.review_content) ?: return
            val left = reviewText.paddingLeft
            val right = parent.width - reviewText.paddingRight
            val top = view.bottom + params.bottomMargin
            mDivider?.let {
                val bottom = top + it.intrinsicHeight
                it.setBounds(left, top, right, bottom)
                it.draw(c)
            }
        }
    }
}