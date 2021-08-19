package com.anothermovieapp.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.anothermovieapp.R

/**
 * Created by Arcturus Mengsk on 2/15/2018, 10:41 AM.
 */
class ReviewsDividerItemDecoration(context: Context) : ItemDecoration() {
    private val mDivider: Drawable
    override fun onDrawOver(c: Canvas, parent: RecyclerView,
                            state: RecyclerView.State) {
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
            val bottom = top + mDivider.intrinsicHeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    init {
        mDivider = context.resources.getDrawable(R.drawable.shadowline)
    }
}