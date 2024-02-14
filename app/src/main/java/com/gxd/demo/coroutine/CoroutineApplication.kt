package com.gxd.demo.coroutine

import android.app.Application
import com.gxd.demo.coroutine.exception.GlobalThreadUncaughtExceptionHandler

class CoroutineApplication : Application() {
    companion object {
        lateinit var instance: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        GlobalThreadUncaughtExceptionHandler.setUp()
    }
}