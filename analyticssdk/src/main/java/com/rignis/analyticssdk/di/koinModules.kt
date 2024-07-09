package com.rignis.analyticssdk.di

import androidx.room.Room
import com.rignis.analyticssdk.config.AnalyticsConfig
import com.rignis.analyticssdk.database.DBAdapter
import com.rignis.analyticssdk.database.DbAdapterImpl
import com.rignis.analyticssdk.database.EventDao
import com.rignis.analyticssdk.database.RignisEventDB
import com.rignis.analyticssdk.network.ApiService
import com.rignis.analyticssdk.network.HeaderInterceptor
import com.rignis.analyticssdk.network.NetworkConnectivityObserver
import com.rignis.analyticssdk.network.NetworkConnectivityObserverImpl
import com.rignis.analyticssdk.worker.AnalyticsWorker
import com.rignis.analyticssdk.worker.AnalyticsWorkerImpl
import com.rignis.analyticssdk.worker.DailySyncScheduler
import com.rignis.analyticssdk.worker.DailySyncSchedulerImpl
import com.rignis.analyticssdk.worker.Syncer
import com.rignis.analyticssdk.worker.SyncerImpl
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration

internal val networkModule = module {
    single<OkHttpClient> {
        val config = get<AnalyticsConfig>()
        OkHttpClient.Builder()
            .callTimeout(Duration.ofMillis(config.syncRequestTimeOut))
            .addInterceptor(HeaderInterceptor(config))
            .build()
    }

    single<Retrofit> {
        val config = get<AnalyticsConfig>()
        Retrofit
            .Builder()
            .baseUrl(config.baseUrl)
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ApiService> {
        val retrofit = get<Retrofit>()
        retrofit.create(ApiService::class.java)
    }

    single<NetworkConnectivityObserver> {
        NetworkConnectivityObserverImpl(get())
    }
}

internal fun configModule(config: AnalyticsConfig) = module {
    single {
        config
    }
}

internal val dbModule = module {
    single<RignisEventDB> {
        Room.databaseBuilder(get(), RignisEventDB::class.java, "rignis_event_db").build()
    }

    single<DBAdapter> {
        DbAdapterImpl(get(), get())
    }

    single<EventDao> {
        get<RignisEventDB>().eventDao()
    }
}

internal val syncerModule = module {
    single<Syncer> {
        SyncerImpl(get(), get(), get(), get())
    }
}

internal val workerModule = module {
    single<AnalyticsWorker> {
        AnalyticsWorkerImpl(get(), get(), get(), get())
    }
    single<DailySyncScheduler> {
        DailySyncSchedulerImpl(get())
    }
}