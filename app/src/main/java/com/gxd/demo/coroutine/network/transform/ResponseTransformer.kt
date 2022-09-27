package com.gxd.demo.coroutine.network.transform

import android.util.Log
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.gxd.demo.coroutine.network.GsonObj
import com.gxd.demo.coroutine.network.data.BaseResponse
import okio.IOException
import java.io.InputStream

class ResponseTransformer : IResponseTransformer {
    @Throws(IOException::class)
    override fun transform(original: InputStream): InputStream {
        val response = GsonObj.gson.fromJson<BaseResponse<JsonElement>>(
            original.reader(), object : TypeToken<BaseResponse<JsonElement>>() {}.type
        )
        Log.d("ggg", "ResponseTransformer.transform...${response.errorCode}...${response.errorMsg}")
        if (response.errorCode != 0 && response.errorCode != 200) throw IOException("errCode = ${response.errorCode}, errMsg = ${response.errorMsg}")

        return response.data.toString().byteInputStream()
    }
}