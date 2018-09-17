package io.sabag.androidConferences.network

import com.google.gson.annotations.SerializedName

data class Content(
        @SerializedName("download_url") val downloadUrl: String? = null
)