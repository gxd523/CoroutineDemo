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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.concurrent.Executors
import kotlin.random.Random
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

suspend fun channelCase(capacity: Int = 3) = coroutineScope {
    val channel = Channel<Int>(capacity)
    launch(singleDispatcher) {
        repeat(capacity) {
            channel.send(it)
            "send".log(it)
        }
        channel.close()// 别忘了Channel用完要关闭
    }
    launch(fixedDispatcher) {
        delay(1_000)
        for (result in channel) {
            "receive1".log(result)
            delay(1_000)
        }
    }
    launch(Dispatchers.IO) {
        delay(1_000)
        for (result in channel) {
            "receive2".log(result)
            delay(2_000)
        }
    }
}

suspend fun flowCase() = coroutineScope {
    val flow = flow {
        repeat(2) {
            val value = Random.nextInt(99)
            emit(value)
            "emit".log(value)
        }
    }.map {
        it.toString()
    }.onStart {
        "onStart".log()
    }.onCompletion {
        "onCompletion".log(it)
    }.catch {
        "catch".log(it)
    }.flowOn(singleDispatcher)

    launch(Dispatchers.IO) {
        flow.collect { "collect1".log(it) }
    }
    launch(fixedDispatcher) {
        flow.collect { "collect2".log(it) }
    }
}

suspend fun channelAsFlowCase(capacity: Int = 3) = coroutineScope {
    val channel = Channel<Int>(capacity)

    launch(Dispatchers.IO) {
        repeat(capacity) {
            channel.send(it)
            "send".log(it)
        }
        channel.close()// 别忘了Channel用完要关闭
    }

    val flow = channel.receiveAsFlow()

    launch(singleDispatcher) {
        flow.collect {
            "collect1".log(it)
        }
    }
    launch(fixedDispatcher) {
        flow.collect {
            "collect2".log(it)
        }
    }
}

suspend fun channelFlowCase() = coroutineScope {
    val flow = channelFlow {
        repeat(2) {
            val value = Random.nextInt(99)
            launch(Dispatchers.IO) {
                send(value)
                "send".log(value)
            }
        }
    }

    launch(singleDispatcher) {
        flow.collect {
            delay(1_000)
            "collect1".log(it)
        }
    }

    launch(fixedDispatcher) {
        flow.collect {
            delay(2_000)
            "collect2".log(it)
        }
    }
}

suspend fun sharedFlowCase(replay: Int = 0, extraBufferCapacity: Int = 3) = coroutineScope {
    val sharedFlow = MutableSharedFlow<Int>(replay, extraBufferCapacity)
    launch(Dispatchers.IO) {
        repeat(replay + extraBufferCapacity) {
            launch(Dispatchers.IO) {
                val value = Random.nextInt(99)
                sharedFlow.emit(value)
                "emit".log(value)
            }
            delay(1_000)
        }
    }

    launch(singleDispatcher) {
//        delay(1)
        withTimeout(5_000) {// collect所在的协程取消才能结束SharedFlow
            sharedFlow.collect {
                "collect1".log(it)
                delay(1_000)
            }
        }
    }

    launch(fixedDispatcher) {
        delay(500)
        withTimeout(5_000) {// collect所在的协程取消才能结束SharedFlow
            sharedFlow.collect {
                "collect2".log(it)
                delay(1_500)
            }
        }
    }
}

suspend fun stateFlowCase() = coroutineScope {
    val stateFlow = MutableStateFlow<Int>(1)
    launch(singleDispatcher) {
        repeat(5) {
            val value = Random.nextInt(99)
            stateFlow.emit(value)
            "emit".log(value)
            delay(1_000)
        }
    }

    launch(Dispatchers.IO) {
        delay(2_000)
        stateFlow.collect {
            "collect1".log(it)
        }
    }

    launch(fixedDispatcher) {
        delay(3_000)
        stateFlow.collect {
            "collect2".log(it)
        }
    }
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