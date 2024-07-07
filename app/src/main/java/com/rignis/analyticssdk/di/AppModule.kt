package com.rignis.analyticssdk.di

import com.rignis.analyticssdk.Analytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun analyticsSDK(): Analytics = Analytics.getInstance()
}
