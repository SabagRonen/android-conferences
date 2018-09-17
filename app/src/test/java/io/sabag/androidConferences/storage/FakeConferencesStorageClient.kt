package io.sabag.androidConferences.storage

import io.sabag.androidConferences.Conference
import io.sabag.androidConferences.ConferenceDetails
import io.sabag.androidConferences.pluginInterfaces.IConferencesStorageClient

class FakeConferencesStorageClient : IConferencesStorageClient {
    private var conferences = listOf<Conference>()
    private val conferencesDetailsList = mutableListOf<ConferenceDetails>()

    override fun getConferences(): List<Conference> {
        return conferences
    }

    override fun addConferences(conferencesList: List<Conference>) {
        val allConferences = listOf(conferences, conferencesList).flatMap { it }
        conferences = allConferences.toSet().toList()
    }

    override fun addConferenceDetails(conferenceDetails: ConferenceDetails) {
        conferencesDetailsList.find {
            conferenceDetails.id == it.id
        }?.apply { conferencesDetailsList.remove(this) }
        conferencesDetailsList.add(conferenceDetails)
    }

    override fun getConferencesDetailsList(): List<ConferenceDetails> {
        return conferencesDetailsList
    }
}