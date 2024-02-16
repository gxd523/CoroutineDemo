package com.gxd.demo.coroutine

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * isLongLog:receiver长度大于15为true
 */
fun Any.log(result: Any? = null, isLongLog: Boolean = false) {
    val time = SimpleDateFormat("mm:ss:SSS", Locale.getDefault()).format(Date())
    val threadInfo = Thread.currentThread().name
    val lastIndex = threadInfo.lastIndexOf('-')
    val formattedThreadInfo = if (lastIndex == -1) {
        threadInfo
    } else {
        val secondLastIndex = threadInfo.lastIndexOf('-', lastIndex - 1)
        threadInfo.substring(secondLastIndex + 1)
    }
    val formattedMsg = if (result?.toString().isNullOrEmpty()) "" else "      result = $result"
    val afterThreadInfoSpaceRepeat = " ".repeat(25 - formattedThreadInfo.length)
    val afterThisSpacerRepeat = " ".repeat(if (isLongLog) 35 else 15 - this.toString().length)
    println("$time\t$formattedThreadInfo$afterThreadInfoSpaceRepeat${this}$afterThisSpacerRepeat$formattedMsg")
}