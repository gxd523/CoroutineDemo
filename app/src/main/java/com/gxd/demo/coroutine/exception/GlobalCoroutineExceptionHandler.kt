package com.gxd.demo.coroutine.exception

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class GlobalCoroutineExceptionHandler : CoroutineExceptionHandler {
    override val key = CoroutineExceptionHandler

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        Log.e("ggg", "Unhandled Coroutine Exception with ${context[Job]}", exception)
    }
}