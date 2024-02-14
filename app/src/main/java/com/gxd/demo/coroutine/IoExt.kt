package com.gxd.demo.coroutine

import java.io.InputStream
import java.io.OutputStream

inline fun InputStream.copyTo(outputStream: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE, progress: (Long) -> Unit): Long {
    var bytesProgress: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        outputStream.write(buffer, 0, bytes)
        bytesProgress += bytes
        bytes = read(buffer)

        progress(bytesProgress)
    }
    return bytesProgress
}