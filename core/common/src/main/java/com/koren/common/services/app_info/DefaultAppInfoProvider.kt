package com.koren.common.services.app_info

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import javax.inject.Inject

class DefaultAppInfoProvider @Inject constructor(
    private val context: Context
) : AppInfoProvider {
    override fun getAppVersion(): String {
        return try {
            val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "Unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }
}