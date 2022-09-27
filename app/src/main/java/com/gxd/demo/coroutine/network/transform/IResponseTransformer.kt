package com.gxd.demo.coroutine.network.transform

import okio.IOException
import java.io.InputStream

interface IResponseTransformer {
    @Throws(IOException::class)
    fun transform(original: InputStream): InputStream
}