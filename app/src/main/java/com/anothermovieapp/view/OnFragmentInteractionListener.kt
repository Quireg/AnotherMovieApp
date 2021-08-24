/*
 * Created by Arcturus Mengsk
 *   2021.
 */

package com.anothermovieapp.view

import java.io.Serializable

interface OnFragmentInteractionListener : Serializable {
    fun onFragmentMessage(tag: String?, data: Any?)
}