package io.sabag.androidConferences

import java.util.Date

data class ConferenceDetails(
        val id: String,
        val name: String,
        val location: String,
        val startDate: Date,
        val endDate: Date?,
        val startCfp: Date?,
        val endCfp: Date?
)