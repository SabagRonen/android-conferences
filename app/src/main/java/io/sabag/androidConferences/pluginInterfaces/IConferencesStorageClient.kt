package io.sabag.androidConferences.pluginInterfaces

import io.sabag.androidConferences.Conference
import io.sabag.androidConferences.ConferenceDetails

interface IConferencesStorageClient {
    fun getConferences(): List<Conference>
    fun addConferences(conferencesList: List<Conference>)
    fun addConferenceDetails(conferenceDetails: ConferenceDetails)
    fun getConferencesDetailsList(): List<ConferenceDetails>
}
