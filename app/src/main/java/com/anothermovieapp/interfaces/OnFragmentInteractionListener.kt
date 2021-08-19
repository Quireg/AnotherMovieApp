package com.anothermovieapp.interfaces

import java.io.Serializable

interface OnFragmentInteractionListener : Serializable {
    fun onFragmentMessage(tag: String?, data: Any?)
}