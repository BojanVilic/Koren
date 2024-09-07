package com.koren.common.services

import android.content.Context
import androidx.annotation.StringRes
import javax.inject.Inject

class ResourceProvider @Inject constructor(private val context: Context) {

    operator fun get(@StringRes stringRes: Int): String {
        return context.getString(stringRes)
    }
}