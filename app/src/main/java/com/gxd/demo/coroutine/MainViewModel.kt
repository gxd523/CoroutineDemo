package com.gxd.demo.coroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _downloadStatus = MutableStateFlow<DownloadManager.DownloadStatus?>(DownloadManager.DownloadStatus.None)
    val downloadStatus = _downloadStatus.asStateFlow()

    fun download(url: String, fileName: String) {
        viewModelScope.launch {
            DownloadManager.download(url, fileName)
                .flowOn(Dispatchers.IO)
                .collect { _downloadStatus.value = it }
        }
    }
}