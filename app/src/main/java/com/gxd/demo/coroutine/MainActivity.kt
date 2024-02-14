package com.gxd.demo.coroutine

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.gxd.demo.coroutine.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        private const val PDF_DOWNLOAD_URL = "https://kotlinlang.org/docs/kotlin-reference.pdf"
        private const val PDF_FILE_NAME = "Kotlin-Docs.pdf"
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mainViewModel.downloadStatusLiveData.observe(this, ::onDownloadStatusChanged)
    }

    fun onAsyncToSyncCaseClick(view: View) {
        mainViewModel.viewModelScope.launch {
            val myChoice = alert("Warning!", "Do you want this?")
            toast("My Choice is $myChoice")
        }
    }

    private fun onDownloadStatusChanged(downloadStatus: DownloadManager.DownloadStatus?) {
        when (downloadStatus) {
            null, DownloadManager.DownloadStatus.None -> with(binding.downloadBtn) {
                isEnabled = true
                text = "Download"
                setOnClickListener {
                    isEnabled = false
                    mainViewModel.download(PDF_DOWNLOAD_URL, PDF_FILE_NAME)
                }
            }

            is DownloadManager.DownloadStatus.Progress -> with(binding.downloadBtn) {
                text = "Downloading...${downloadStatus.progress}%"
            }

            is DownloadManager.DownloadStatus.Done -> with(binding.downloadBtn) {
                isEnabled = true
                text = "Open File"
                setOnClickListener {
                    Intent(Intent.ACTION_VIEW).also {
                        val authority = "${packageName}.provider"
                        val uriForFile = FileProvider.getUriForFile(this@MainActivity, authority, downloadStatus.file)
                        it.setDataAndType(uriForFile, "application/pdf")
                        it.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }.let(::startActivity)
                }
                toast(downloadStatus.file)
            }

            is DownloadManager.DownloadStatus.Error -> with(binding.downloadBtn) {
                isEnabled = true
                text = "Download Error"
                setOnClickListener { mainViewModel.download(PDF_DOWNLOAD_URL, PDF_FILE_NAME) }
                toast(downloadStatus.throwable)
            }
        }
    }
}