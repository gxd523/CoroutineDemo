package com.gxd.demo.coroutine

import android.os.Environment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

object DownloadManager {
    private val downloadDirectory by lazy {
        CoroutineApplication.instance.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.also { it.mkdirs() }
    }

    fun download(url: String, fileName: String): Flow<DownloadStatus> {
        val file = File(downloadDirectory, fileName)
        return flow {
            val request = Request.Builder().url(url).get().build()
            val response = OkHttpClient.Builder().build().newCall(request).execute()
            if (!response.isSuccessful) throw Exception("response not successful")
            val responseBody = response.body() ?: throw Exception("response body null")

            val total = responseBody.contentLength()
            var emittedProgress = -1L

            file.outputStream().use { output ->
                responseBody.byteStream().use { input ->
                    input.copyTo(output) { bytesProgress ->
                        val progress = bytesProgress * 100 / total
                        if (progress == emittedProgress) return@copyTo
                        emit(DownloadStatus.Progress(progress.toInt()))
                        emittedProgress = progress
                    }
                }
            }
            emit(DownloadStatus.Done(file))
        }.catch { throwable ->
            file.delete()
            emit(DownloadStatus.Error(throwable))
        }.conflate()
    }

    sealed class DownloadStatus {
        data object None : DownloadStatus()
        data class Progress(val progress: Int) : DownloadStatus()
        data class Error(val throwable: Throwable) : DownloadStatus()
        data class Done(val file: File) : DownloadStatus()
    }
}