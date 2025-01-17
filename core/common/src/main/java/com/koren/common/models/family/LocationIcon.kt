package com.koren.common.models.family

import androidx.annotation.DrawableRes
import com.koren.common.R

enum class LocationIcon(@DrawableRes val drawableResId: Int) {
    APARTMENT(R.drawable.apartment),
    CABIN(R.drawable.cabin),
    COLOSSEUM(R.drawable.colosseum),
    HOUSE(R.drawable.house),
    HOUSE_2(R.drawable.house_2),
    HOUSE_3(R.drawable.house_3),
    HOUSE_4(R.drawable.house_4),
    HUT(R.drawable.hut),
    IGLOO(R.drawable.igloo),
    LIGHTHOUSE(R.drawable.lighthouse),
    SKYSCRAPER(R.drawable.skyscraper),
    DEFAULT(R.drawable.house);

    companion object {
        fun fromString(iconName: String): LocationIcon =
            entries.firstOrNull { it.name.equals(iconName, ignoreCase = true) } ?: DEFAULT
    }
}