package com.koren.data.repository

import com.koren.common.models.activity.BaseActivity

interface ActivityRepository {
    fun insertNewActivity(activity: BaseActivity)
    fun getActivities(): List<BaseActivity>
}