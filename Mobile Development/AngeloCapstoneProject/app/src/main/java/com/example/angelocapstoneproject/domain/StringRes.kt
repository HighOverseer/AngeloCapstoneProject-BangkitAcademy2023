package com.example.angelocapstoneproject.domain

import android.content.Context

sealed class StringRes {
    fun getValue(context: Context): String {
        return when (this) {
            is DynamicString -> stringVal
            is StaticString -> {
                this.arg?.let {
                    context.getString(stringResId, it)
                } ?: context.getString(stringResId)

            }
        }
    }

}

class DynamicString(val stringVal: String) : StringRes()
class StaticString(val stringResId: Int, val arg: String? = null) : StringRes()
