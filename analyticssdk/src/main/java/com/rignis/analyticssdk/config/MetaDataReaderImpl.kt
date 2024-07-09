/*
 * Copyright (c) [2024] Amitkumar Chaudhary
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rignis.analyticssdk.config

import android.content.Context
import android.content.pm.PackageManager

internal class MetaDataReaderImpl(context: Context) : MetaDataReader {
    private val applicationInfo = context.packageManager.getApplicationInfo(
        context.packageName,
        PackageManager.GET_META_DATA
    )
    private val metaDataBundle = applicationInfo.metaData

    override fun getClientId(): String? {
        return metaDataBundle?.getString(META_DATA_CLIENT_ID)
    }

    companion object {
        private const val META_DATA_CLIENT_ID = "com.rignis.analyticssdk.clientid"
    }
}