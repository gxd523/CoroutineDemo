package com.gxd.demo.coroutine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _downloadStatusLiveData = MutableLiveData<DownloadManager.DownloadStatus?>(DownloadManager.DownloadStatus.None)
    val downloadStatusLiveData: LiveData<DownloadManager.DownloadStatus?> = _downloadStatusLiveData

    fun download(url: String, fileName: String) {
        viewModelScope.launch {
            DownloadManager.download(url, fileName)
                .flowOn(Dispatchers.IO)
                .collect { _downloadStatusLiveData.value = it }
        }
    }
}