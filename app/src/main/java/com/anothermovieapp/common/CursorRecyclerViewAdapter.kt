package com.anothermovieapp.common

import android.database.Cursor
import android.database.DataSetObserver
import androidx.recyclerview.widget.RecyclerView

/*
 * Copyright (C) 2014 skyfish.jy@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */ /**
 * Created by skyfishjy on 10/31/14.
 */
abstract class CursorRecyclerViewAdapter<VH : RecyclerView.ViewHolder?>(var cursor: Cursor?) : RecyclerView.Adapter<VH>() {
    private var mDataValid: Boolean
    private val mDataSetObserver: DataSetObserver?

    override fun getItemCount(): Int {
        return if (mDataValid && cursor != null) {
            cursor!!.count
        } else 0
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    abstract fun onBindViewHolder(viewHolder: VH, cursor: Cursor?)
    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        check(mDataValid) { "this should only be called when the cursor is valid" }
        check(cursor!!.moveToPosition(position)) { "couldn't move cursor to position $position" }
        onBindViewHolder(viewHolder, cursor)
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    private fun changeCursor(cursor: Cursor) {
        val old = swapCursor(cursor)
        old?.close()
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * [.changeCursor], the returned old Cursor is *not*
     * closed.
     */
    fun swapCursor(newCursor: Cursor): Cursor? {
        if (newCursor === cursor) {
            return null
        }
        val oldCursor = cursor
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver)
        }
        cursor = newCursor
        if (cursor != null) {
            if (mDataSetObserver != null) {
                cursor!!.registerDataSetObserver(mDataSetObserver)
            }
            mDataValid = true
            notifyDataSetChanged()
        } else {
            mDataValid = false
            notifyDataSetChanged()
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor
    }

    private inner class NotifyingDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            mDataValid = true
            notifyDataSetChanged()
        }

        override fun onInvalidated() {
            super.onInvalidated()
            mDataValid = false
            notifyDataSetChanged()
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }

    init {
        mDataValid = cursor != null
        mDataSetObserver = NotifyingDataSetObserver()
        if (cursor != null) {
            cursor!!.registerDataSetObserver(mDataSetObserver)
        }
    }
}