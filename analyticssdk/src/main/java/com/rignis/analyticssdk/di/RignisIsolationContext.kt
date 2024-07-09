package com.rignis.analyticssdk.di

import org.koin.core.KoinApplication
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

object RignisIsolationContext {
    internal lateinit var koinApp: KoinApplication

    fun startKoin(koinAppDeclaration: KoinAppDeclaration) {
        koinApp = koinApplication(koinAppDeclaration)
    }

    val koin
        get() = koinApp.koin
}