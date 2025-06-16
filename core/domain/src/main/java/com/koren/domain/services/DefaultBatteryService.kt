package com.koren.domain.services

import android.content.Context
import android.os.BatteryManager
import com.koren.common.services.BatteryService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DefaultBatteryService @Inject constructor(
    @ApplicationContext
    private val context: Context
) : BatteryService {
    override fun getCurrentBatteryLevel(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
}