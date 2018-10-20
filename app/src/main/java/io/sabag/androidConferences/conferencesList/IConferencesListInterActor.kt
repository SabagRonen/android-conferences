package io.sabag.androidConferences.conferencesList

import java.util.Date

enum class CfpStatus {
    CFP_STATUS_NA, CFP_STATUS_NOT_STARTED, CFP_STATUS_IN_PROGRESS, CFP_STATUS_ENDED
}

data class ConferenceDetailsData(
        val id: String,
        val name: String,
        val location: String,
        val startDate: Date,
        val endDate: Date?,
        val cfpStatus: CfpStatus
)

interface IConferencesListInterActor {
    fun observeConferences(shouldObserveFuture: Boolean, observer: (List<ConferenceDetailsData>) -> Unit)
}
