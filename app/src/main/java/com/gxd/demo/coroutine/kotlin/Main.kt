package com.gxd.demo.coroutine.kotlin

import com.gxd.demo.coroutine.log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.time.Duration

val singleDispatcher = Executors.newSingleThreadExecutor { Thread(it, "SingleThread") }.asCoroutineDispatcher()

@OptIn(DelicateCoroutinesApi::class)
val fixedDispatcher = newFixedThreadPoolContext(2, "FixedThreadPool")

val exceptionHandler = CoroutineExceptionHandler { _, exception ->
    "Caught exception: ${exception.message}".log()
}

val scope = CoroutineScope(Job() + exceptionHandler + singleDispatcher)

@OptIn(ExperimentalCoroutinesApi::class)
fun main(): Unit = runBlocking {
    "start".log()

    "end".log()
}

fun <T> Flow<T>.throttle(time: Duration): Flow<T> = flow {
    var lastTime = 0L
    this@throttle.collect {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime > time.inWholeMilliseconds) {
            emit(it)
            lastTime = currentTime
        }
    }
}

suspend fun sharedFlowCase() {
    val sharedHotFlow = MutableStateFlow(0)
    coroutineScope {
        "2".log()
        launch(Dispatchers.IO) {
            repeat(5) {
                "emit".log(it)
                sharedHotFlow.emit(it)
                delay(500)
            }
        }

        "sss".log()
        launch {
            "3".log()
            sharedHotFlow.collect {
                "a".log(it)
            }
        }
        delay(1_000)

        launch {
            "4".log()
            sharedHotFlow.collect {
                "b".log(it)
            }
        }
    }
    "eee".log()
}

suspend fun flowCase() {
    val intFlow = flow {
        fetchData(1_000, 1)
        emit(1)
        emit(2)
        emit(3)
        throw Exception("Div 0")
    }.map {
        "map".log(it.toString())
        it.toString()
    }.catch { throwable ->
        "flow exception".log(throwable)
    }.flowOn(Dispatchers.IO).onStart {
        "flow start".log()
    }.onCompletion { throwable ->// 加了catch{}，throwable就为null
        "flow completion".log(throwable)
    }

    intFlow.collect { result -> "collect".log(result) }
}

suspend fun fetchData(delay: Long, returnData: Int): Int {
    delay(delay)
    return returnData
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
    "isActive = ${job.isActive}, isCompleted = ${job.isCompleted}, isCancelled = ${job.isCancelled}".log()
    job.start()
    "isActive = ${job.isActive}, isCompleted = ${job.isCompleted}, isCancelled = ${job.isCancelled}".log()
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
        val result = listOf(job1, job2, job3).awaitAll().toString()

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

/**
 * 取消Job的生命周期状态
 */
suspend fun testCancelJobLifeCycle() {
    coroutineScope {
        val job = launch(start = CoroutineStart.LAZY) {
            "coroutine start".log()
            delay(1_000)
            "coroutine end".log()
        }
        job.log("New")
        job.start()
        job.log("Active")
        delay(500)
        job.log("Completing")
        job.cancel()
        job.log("Cancelling")
        job.invokeOnCompletion {// 使用join()或invokeOnCompletion{}监听结束
            job.log("Canceled")
        }
    }
}

/**
 * 正常Job的生命周期状态
 */
suspend fun testCompleteJobLifeCycle() {
    coroutineScope {
        val job = launch(start = CoroutineStart.LAZY) {
            "coroutine start".log()
            delay(1_000)
            "coroutine end".log()
        }
        job.log("New")
        job.start()
        job.log("Active")
        delay(500)
        job.log("Completing")
        job.join()// 使用join()或invokeOnCompletion{}监听结束
        job.log("Completed")
    }
}