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
package com.rignis.analyticssdk

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth
import com.rignis.analyticssdk.RignisAnalytics.config
import com.rignis.analyticssdk.database.DBAdapter
import com.rignis.analyticssdk.di.RignisKoinComponent
import com.rignis.analyticssdk.di.configModule
import com.rignis.analyticssdk.di.dbModule
import com.rignis.analyticssdk.di.networkModule
import com.rignis.analyticssdk.di.syncerModule
import com.rignis.analyticssdk.di.workerModule
import com.rignis.analyticssdk.mock.MockMetaDataReader
import com.rignis.analyticssdk.worker.AnalyticsWorker
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTestRule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class RignisAnalyticsApiTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            networkModule,
            workerModule,
            syncerModule,
            configModule(config),
            dbModule,
        )
    }

    @Before
    fun before() {
        RignisAnalytics.setBaseUrl("http://192.168.0.194:3000")
        RignisAnalytics.setForegroundSyncInterval(5000)
        RignisAnalytics.setForegroundSyncBatchSize(5)
        RignisAnalytics.metaDataReaderBuilder = {
            MockMetaDataReader
        }
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        RignisAnalytics.initialize(appContext)
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.rignis.analyticssdk.test", appContext.packageName)
    }


    @Test
    fun triggerAnalyticsEventImmediatelyAfterBatchIsFull() {
        val analyticsWorker by RignisKoinComponent.inject<AnalyticsWorker>(AnalyticsWorker::class.java)
        val dbAdapter by RignisKoinComponent.inject<DBAdapter>(DBAdapter::class.java)
        analyticsWorker.sendEvent("test_event", mapOf("counter" to "1"))
        analyticsWorker.sendEvent("test_event", mapOf("counter" to "2"))
        analyticsWorker.sendEvent("test_event", mapOf("counter" to "3"))
        analyticsWorker.sendEvent("test_event", mapOf("counter" to "4"))
        analyticsWorker.sendEvent("test_event", mapOf("counter" to "5"))
        Thread.sleep(1000)
        Truth.assertThat(dbAdapter.getTotalEventCount()).isEqualTo(0)
    }

}