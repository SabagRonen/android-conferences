package io.sabag.androidConferences.storage

import io.sabag.androidConferences.Conference
import io.sabag.androidConferences.ConferenceDetails
import io.sabag.androidConferences.pluginInterfaces.IConferencesStorageClient

class RoomConferencesStorageClient(
        private val conferencesDao: ConferencesDao,
        private val conferenceDetailsDao: ConferenceDetailsDao
) : IConferencesStorageClient {
    override fun getConferences() =
            conferencesDao.getConferences().map { Conference(it.id) }

    override fun addConferences(conferencesList: List<Conference>) {
        conferencesDao.addConferences(conferencesList.map { RoomConference(it.conferenceId) })
    }

    override fun addConferenceDetails(conferenceDetails: ConferenceDetails) {
        conferenceDetailsDao.addConferenceDetails(conferenceDetails.toRoomConferenceDetails())
    }

    override fun getConferencesDetailsList() =
            conferenceDetailsDao.getConferenceDetails().map { it.toConferenceDetails() }

    private fun RoomConferenceDetails.toConferenceDetails() =
            ConferenceDetails (
                    id = this.id,
                    name = this.name,
                    location = this.location,
                    startDate = this.startDate,
                    endDate = this.endDate,
                    startCfp = this.cfpStart,
                    endCfp = this.cfpEnd
            )

    private fun ConferenceDetails.toRoomConferenceDetails() =
            RoomConferenceDetails (
                    id = this.id,
                    name = this.name,
                    location = this.location,
                    startDate = this.startDate,
                    endDate = this.endDate,
                    cfpStart = this.startCfp,
                    cfpEnd = this.endCfp
            )
}
