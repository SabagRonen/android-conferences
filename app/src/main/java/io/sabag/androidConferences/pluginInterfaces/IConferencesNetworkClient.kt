package io.sabag.androidConferences.pluginInterfaces

import io.sabag.androidConferences.Conference
import io.sabag.androidConferences.ConferenceDetails

interface IConferencesNetworkClient {
    fun getConferencesList(): List<Conference>
    fun getConferenceDetails(conferenceId: String): ConferenceDetails?
}
