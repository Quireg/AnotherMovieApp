package com.anothermovieapp.common

object StringUtils {
    fun cutIfLonger(str: String, maxLength: Int): String {
        return if (str.length > maxLength) {
            str.substring(0, maxLength) + "..."
        } else str
    }

    fun isEmpty(s: CharSequence?): Boolean {
        return s == null || "" == s
    }

    fun isEmptyOrWhiteSpace(s: String?): Boolean {
        return isEmpty(emptyIfNull(s).trim { it <= ' ' })
    }

    fun emptyIfNull(s: CharSequence?): String {
        return s?.toString() ?: ""
    }

    fun nullIfEmpty(s: String?): String? {
        return if (isEmpty(s)) null else s
    }

    fun areEqual(s1: String?, s2: String?): Boolean {
        return s1 == null && s2 == null || s1 != null && s1 == s2
    }
}