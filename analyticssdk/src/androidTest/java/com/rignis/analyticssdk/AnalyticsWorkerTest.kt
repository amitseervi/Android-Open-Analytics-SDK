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
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.database.DBAdapter
import com.rignis.analyticssdk.di.configModule
import com.rignis.analyticssdk.di.dbModule
import com.rignis.analyticssdk.di.networkModule
import com.rignis.analyticssdk.di.workerModule
import com.rignis.analyticssdk.network.NetworkConnectivityObserver
import com.rignis.analyticssdk.worker.AnalyticsWorker
import com.rignis.analyticssdk.worker.Syncer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import org.koin.test.KoinTestRule


@RunWith(AndroidJUnit4::class)
class AnalyticsWorkerTest {
    internal class TestSyncer : Syncer {
        var syncerCallCount = 0
        override fun sync(callback: Syncer.OnRequestCompleteCallback) {
            syncerCallCount++
            callback.onSuccess()
        }

    }

    internal class TestNetworkConnectivityObserver : NetworkConnectivityObserver {
        override val isNetworkAvailable: Boolean = true

        override fun addCallback(callback: NetworkConnectivityObserver.Callback) {
            // do not send callbacks because it add another complexity while testing
        }
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        androidContext(appContext)
        modules(
            networkModule,
            workerModule,
            configModule(AnalyticsConfig().apply {
                foregroundSyncBatchSize = 10
                foregroundSyncInterval = 5000
            }),
            dbModule,
            module {
                single<Syncer> { TestSyncer() }
            },
            module {
                single<NetworkConnectivityObserver> {
                    TestNetworkConnectivityObserver()
                }
            }
        )
    }

    @Test
    fun testAnalyticsWorkerBatchFullEventSyncTrigger() {
        val analyticsWorker by inject<AnalyticsWorker>(AnalyticsWorker::class.java)
        val syncer by inject<Syncer>(Syncer::class.java)
        Truth.assertThat(syncer).isInstanceOf(TestSyncer::class.java)
        val dbAdapter by inject<DBAdapter>(DBAdapter::class.java)
        dbAdapter.clearAllEvents()
        Truth.assertThat(dbAdapter.getTotalEventCount()).isEqualTo(0)
        repeat(10) {
            analyticsWorker.sendEvent("test_event", mapOf())
        }
        Thread.sleep(500)
        analyticsWorker.close()
        Truth.assertThat((syncer as TestSyncer).syncerCallCount).isEqualTo(1)
    }


    @Test
    fun testSingleEventShouldGetTriggeredAfterSyncInterval() {
//        Timber.plant(Timber.DebugTree())
        val analyticsWorker by inject<AnalyticsWorker>(AnalyticsWorker::class.java)
        val syncer by inject<Syncer>(Syncer::class.java)
        Truth.assertThat(syncer).isInstanceOf(TestSyncer::class.java)
        val dbAdapter by inject<DBAdapter>(DBAdapter::class.java)
        dbAdapter.clearAllEvents()
        Truth.assertThat(dbAdapter.getTotalEventCount()).isEqualTo(0)
        repeat(1) {
            analyticsWorker.sendEvent("test_event", mapOf())
        }
        Thread.sleep(4000)
        Truth.assertThat((syncer as TestSyncer).syncerCallCount).isEqualTo(0)
        Thread.sleep(5000)
        Truth.assertThat((syncer as TestSyncer).syncerCallCount).isEqualTo(1)
    }


    @Test
    fun triggerAnalyticsEventImmediatelyAfterBatchIsFull() {
        val analyticsWorker by inject<AnalyticsWorker>(AnalyticsWorker::class.java)
        val syncer by inject<Syncer>(Syncer::class.java)
        Truth.assertThat(syncer).isInstanceOf(TestSyncer::class.java)
        val dbAdapter by inject<DBAdapter>(DBAdapter::class.java)
        dbAdapter.clearAllEvents()
        Truth.assertThat(dbAdapter.getTotalEventCount()).isEqualTo(0)
        repeat(10){index->
            analyticsWorker.sendEvent("test_event", mapOf("counter" to "${index + 1}"))
        }
        Thread.sleep(500)
        Truth.assertThat((syncer as TestSyncer).syncerCallCount).isEqualTo(1)
    }
}