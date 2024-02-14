package com.gxd.demo.coroutine

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.log(result: String? = null, isLongLog: Boolean = false) {
    val time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date())
    val threadInfo = Thread.currentThread().name
    val lastIndex = threadInfo.lastIndexOf('-')
    val formattedThreadInfo = if (lastIndex == -1) {
        threadInfo
    } else {
        val secondLastIndex = threadInfo.lastIndexOf('-', lastIndex - 1)
        threadInfo.substring(secondLastIndex + 1)
    }
    val formattedMsg = if (result.isNullOrEmpty()) "" else "      result = $result"
    val afterThreadInfoSpaceRepeat = " ".repeat(25 - formattedThreadInfo.length)
    val afterThisSpacerRepeat = " ".repeat(if (isLongLog) 35 else 5 - this.length)
    println("$time\t$formattedThreadInfo$afterThreadInfoSpaceRepeat$this$afterThisSpacerRepeat$formattedMsg")
}