package com.gxd.demo.coroutine.network.data

import com.google.gson.annotations.SerializedName

data class BannerData(
    val id: Long? = null,
    val title: String? = null,
    @SerializedName("imagePath")
    val imageUrl: String? = null,
    val desc: String? = null
)