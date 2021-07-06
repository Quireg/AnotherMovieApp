package com.anothermovieapp.common

import android.util.Log

object MovieAppLogger {
    fun d(tag: String?, msg: String?) {
        if (Constants.LOGGING) {
            Log.d(tag, StringUtils.emptyIfNull(msg))
        }
    }

    fun i(tag: String?, msg: String?) {
        if (Constants.LOGGING) {
            Log.i(tag, StringUtils.emptyIfNull(msg))
        }
    }

    fun e(tag: String?, msg: String?) {
        if (Constants.LOGGING) {
            Log.e(tag, StringUtils.emptyIfNull(msg))
        }
    }

    fun e(tag: String?, e: Throwable) {
        if (Constants.LOGGING) {
            Log.e(tag, e.toString())
        }
    }

    fun w(tag: String?, msg: String?) {
        if (Constants.LOGGING) {
            Log.w(tag, msg)
        }
    }

    fun v(tag: String?, msg: String?) {
        if (Constants.LOGGING && false) {
            Log.v(tag, msg)
        }
    }
}