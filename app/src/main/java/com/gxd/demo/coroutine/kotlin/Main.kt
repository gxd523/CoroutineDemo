package com.gxd.demo.coroutine.kotlin

import com.gxd.demo.coroutine.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun main() {
    runBlocking {
        lazyLaunchCase()
//        asyncCase()
//        runBlockingCase()
//        concurrentTaskCase()
//        serialTaskCase()
    }
}

fun CoroutineScope.lazyLaunchCase() {
    "a".log()
    val job = launch(start = CoroutineStart.LAZY) {
        "b".log()
        val result = withContext(Dispatchers.IO) {
            "c".log()
            delay(2_000)
            "d".log()
            "Hello Coroutine!"
        }
        "e".log(result)
    }
    "isActive = ${job.isActive}, isCompleted = ${job.isCompleted}, isCancelled = ${job.isCancelled}".log(isLongLog = true)
    job.start()
    "isActive = ${job.isActive}, isCompleted = ${job.isCompleted}, isCancelled = ${job.isCancelled}".log(isLongLog = true)
}

fun CoroutineScope.asyncCase() {
    "a".log()
    launch {
        "b".log()
        val deferred = async {
            "c".log()
            delay(2_000)
            "d".log()
            "Hello Coroutine!"
        }
        "e".log()
        val result = deferred.await()
        "f".log(result)
    }
    "h".log()
}

fun runBlockingCase() {
    "a".log()
    val result = runBlocking {
        "b".log()
        delay(2_000)
        "c".log()
        "Hello Coroutine!"
    }
    "d".log(result)
}

fun CoroutineScope.concurrentTaskCase() {
    "a".log()
    launch {
        "b".log()
        val job1 = async(Dispatchers.IO) {
            "c".log()
            delay(1_000)
            "d".log()
            1
        }
        "e".log()
        val job2 = async(Dispatchers.IO) {
            "f".log()
            delay(3_000)
            "g".log()
            2
        }
        "h".log()
        val job3 = async(Dispatchers.IO) {
            "i".log()
            delay(5_000)
            "j".log()
            3
        }
        "k".log()

        val result = "${job1.await()} ${job2.await()} ${job3.await()}"

        "m".log(result)
    }
    "n".log()
}

fun CoroutineScope.serialTaskCase() {
    "a".log()
    launch {
        "b".log()
        val resultA = withContext(Dispatchers.IO) {
            "c".log()
            delay(1_000)
            "d".log()
            "aaa"
        }
        "e".log()
        val resultB = withContext(Dispatchers.IO) {
            "f".log()
            delay(2_000)
            "g".log()
            "bbb"
        }
        "h".log()
        val result = "$resultA $resultB"
        "i".log(result)
    }
    "j".log()
}