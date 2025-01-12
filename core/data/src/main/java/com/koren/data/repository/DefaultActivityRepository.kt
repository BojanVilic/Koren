package com.koren.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.koren.common.models.activity.BaseActivity
import javax.inject.Inject

class DefaultActivityRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : ActivityRepository {

    override fun insertNewActivity(activity: BaseActivity) {
    }

    override fun getActivities(): List<BaseActivity> {

    }
}