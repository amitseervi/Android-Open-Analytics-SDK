package com.rignis.analyticssdk.config

import android.content.Context
import android.content.pm.PackageManager

class MetaDataReader(context: Context) {
    private val applicationInfo = context.packageManager.getApplicationInfo(
        context.packageName,
        PackageManager.GET_META_DATA
    )
    private val metaDataBundle = applicationInfo.metaData

    fun getClientId(): String? {
        return metaDataBundle.getString(META_DATA_CLIENT_ID)
    }

    companion object {
        private const val META_DATA_CLIENT_ID = "com.rignis.analyticssdk.clientid"

    }
}