package io.sabag.androidConferences.network

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ConferenceDetails(
        @SerializedName("name") val name: String? = null,
        @SerializedName("website") val website: String? = null,
        @SerializedName("location") val location: String? = null,
        @SerializedName("date_start") val dateStart: Date? = null,
        @SerializedName("date_end") val dateEnd: Date? = null,
        @SerializedName("cfp_start") val cfpStart: Date? = null,
        @SerializedName("cfp_end") val cfpEnd: Date? = null,
        @SerializedName("cfp_site") val cfpSite: String? = null
)