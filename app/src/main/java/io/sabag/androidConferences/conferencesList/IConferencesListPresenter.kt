package io.sabag.androidConferences.conferencesList

import androidx.lifecycle.Lifecycle
import io.sabag.androidConferences.ConferenceState

interface IConferencesListPresenter {
    fun loadData()
    fun observeConferencesState(lifecycle: Lifecycle, observer: (List<ConferenceState>) -> Unit)
    fun pastButtonClicked()
    fun upcomingButtonClicked()
}