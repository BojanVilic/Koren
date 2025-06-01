package com.koren.common.util

import okhttp3.internal.immutableListOf

object Constants {
    val DEFAULT_FREQUENCY_OPTIONS = immutableListOf(1, 5, 15, 30, 60)
    const val DEFAULT_LOCATION_UPDATE_FREQUENCY_IN_MINS = 15
}