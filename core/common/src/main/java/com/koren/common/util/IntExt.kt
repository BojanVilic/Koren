package com.koren.common.util

import java.util.Locale

fun Int?.formatDistanceToText(): String {
    if (this == null) return "Unknown distance"
    val distance = this.toLong()
    var distanceString = "${distance}m away"

    if (distance > 2000) {
        val distanceKm = distance / 1000.0
        distanceString = String.format(Locale.getDefault(), "%.1fkm away", distanceKm)
    }
    return distanceString
}