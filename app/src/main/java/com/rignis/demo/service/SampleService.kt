package com.rignis.demo.service

import android.app.Service
import android.content.Intent
import android.os.HandlerThread
import android.os.IBinder

class SampleService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

    }
}