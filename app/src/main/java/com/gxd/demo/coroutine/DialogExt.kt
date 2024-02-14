package com.gxd.demo.coroutine

import android.app.AlertDialog
import android.content.Context
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun Context.alert(title: String, message: String): Boolean = suspendCancellableCoroutine { continuation ->
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            continuation.resume(false)
        }
        .setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            continuation.resume(true)
        }
        .setOnDismissListener { continuation.resume(false) }
        .create()
        .also { dialog ->
            continuation.invokeOnCancellation { dialog.dismiss() }
        }
        .show()
}