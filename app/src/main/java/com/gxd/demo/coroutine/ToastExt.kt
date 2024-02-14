package com.gxd.demo.coroutine

import android.content.Context
import android.widget.Toast

fun Context.toast(message: Any?) {
    Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
}