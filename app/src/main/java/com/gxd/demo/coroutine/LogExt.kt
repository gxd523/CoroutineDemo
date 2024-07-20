package com.gxd.demo.coroutine

import kotlinx.coroutines.Job
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Job.log(suffix: String) {
    "isActive = $isActive, isCancelled = $isCancelled, isCompleted = $isCompleted".tableFormat(16).log(suffix)
}


fun Any.log(result: Any? = null) {
    val time = SimpleDateFormat("mm:ss:SSS", Locale.getDefault()).format(Date())

    val threadInfo = Thread.currentThread().name
    val lastIndex = threadInfo.lastIndexOf('-')
    val formatThreadInfo = if (lastIndex == -1) {
        threadInfo
    } else {
        val secondLastIndex = threadInfo.lastIndexOf('-', lastIndex - 1)
        threadInfo.substring(secondLastIndex + 1)
    }

    val formatResult = result ?: ""
    val message = "${time.tableFormat(3)}${formatThreadInfo.tableFormat(6)}${this.toString().tableFormat()}$formatResult"

    println(message)
}

fun String.tableFormat(tableCount: Int = 5): String {
    val repeatCount = tableCount - this.length / 4
    val separator = "\t".accumulate(if (repeatCount < 0) 0 else repeatCount)
    return "$this$separator"
}

fun String.accumulate(repeatCount: Int): String = if (repeatCount == 0) "" else this + accumulate(repeatCount - 1)