package io.sabag.androidConferences.conferencesList

import io.sabag.androidConferences.ConferenceDetails

interface IConferencesListInterActor {
    fun observeConferences(observer: (List<ConferenceDetails>) -> Unit)
}
