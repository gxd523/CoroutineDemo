package com.gxd.demo.coroutine.exception

import android.util.Log

class GlobalThreadUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
    companion object {
        fun setUp() {
            Thread.setDefaultUncaughtExceptionHandler(GlobalThreadUncaughtExceptionHandler())
        }
    }

    /**
     * Don't use lazy here.
     */
    private val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.d("ggg", "Uncaugth exception in thread: ${t.name}", e)
        defaultUncaughtExceptionHandler?.uncaughtException(t, e)
    }
}