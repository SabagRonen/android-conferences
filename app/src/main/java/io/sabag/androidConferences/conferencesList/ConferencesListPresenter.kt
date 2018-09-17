package io.sabag.androidConferences.conferencesList

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import io.sabag.androidConferences.ConferenceDetails
import io.sabag.androidConferences.ConferenceState
import io.sabag.androidConferences.pluginInterfaces.ITimeAndDateUtils

class ConferencesListPresenter(
        private val interActor: IConferencesListInterActor,
        private val timeAndDateUtils: ITimeAndDateUtils
) : IConferencesListPresenter {
    private val conferencesStateLiveData = MutableLiveData<List<ConferenceState>>()

    override fun observeConferencesState(lifecycle: Lifecycle, observer: (List<ConferenceState>) -> Unit) {
        conferencesStateLiveData.observe({lifecycle}) {
            observer(it)
        }
    }

    override fun loadData() {
        interActor.observeConferences{ conferencesDetailsList ->
            val list = conferencesDetailsList.map {
                ConferenceState(
                        title = it.name,
                        location = it.location,
                        dates = getDates(it)
                )
            }
            conferencesStateLiveData.postValue(list)
        }
    }

    override fun pastButtonClicked() {

    }

    override fun upcomingButtonClicked() {

    }

    private fun getDates(conferenceDetails: ConferenceDetails) =
            conferenceDetails.endDate?.let {
                "${timeAndDateUtils.getFormattedDate(conferenceDetails.startDate)} - " +
                        "${timeAndDateUtils.getFormattedDate(it)}"
        } ?: timeAndDateUtils.getFormattedDate(conferenceDetails.startDate)
}
