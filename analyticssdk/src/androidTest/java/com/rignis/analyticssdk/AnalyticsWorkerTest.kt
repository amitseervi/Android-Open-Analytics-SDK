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
import com.rignis.analyticssdk.worker.AnalyticsWorker
import com.rignis.analyticssdk.worker.Syncer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import org.koin.test.KoinTestRule
import timber.log.Timber


@RunWith(AndroidJUnit4::class)
class AnalyticsWorkerTest {
    internal class TestSyncer : Syncer {
        var syncerCallCount = 0
        override fun sync(callback: Syncer.OnRequestCompleteCallback) {
            syncerCallCount++
            callback.onSuccess()
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
            }),
            dbModule,
            module {
                single<Syncer> { TestSyncer() }
            }
        )
    }

    @Test
    fun testAnalyticsWorkerBatchFullEventSyncTrigger() {
//        Timber.plant(Timber.DebugTree())
        val analyticsWorker by inject<AnalyticsWorker>(AnalyticsWorker::class.java)
        val syncer by inject<Syncer>(Syncer::class.java)
        Truth.assertThat(syncer).isInstanceOf(TestSyncer::class.java)
        val dbAdapter by inject<DBAdapter>(DBAdapter::class.java)
        dbAdapter.clearAllEvents()
        Truth.assertThat(dbAdapter.getTotalEventCount()).isEqualTo(0)
        repeat(10) {
            analyticsWorker.sendEvent("test_event", mapOf())
        }
        Thread.sleep(1000)
        analyticsWorker.close()
        Truth.assertThat((syncer as TestSyncer).syncerCallCount).isEqualTo(1)
    }
}