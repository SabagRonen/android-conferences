package io.sabag.androidConferences.conferencesList

import io.sabag.androidConferences.ConferenceDetails
import io.sabag.androidConferences.pluginInterfaces.IConferencesNetworkClient
import io.sabag.androidConferences.pluginInterfaces.IConferencesStorageClient
import io.sabag.androidConferences.pluginInterfaces.ITimeAndDateUtils
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach

class UseCase {
    lateinit var networkClient: IConferencesNetworkClient
    lateinit var storageClient: IConferencesStorageClient
    lateinit var taskRunner: TaskRunner
    lateinit var timeAndDateUtils: ITimeAndDateUtils
    private val dataSource: DataSource<List<ConferenceDetailsData>> by lazy {
        DataSource<List<ConferenceDetailsData>>(taskRunner)
    }

    private fun List<ConferenceDetails>.toConferenceDetailsData() = map {
        val cfpStatus = if (it.startCfp == null || it.endCfp == null) {
            CfpStatus.CFP_STATUS_NA
        } else if (it.startCfp.time > timeAndDateUtils.currentTimeInMilliseconds) {
            CfpStatus.CFP_STATUS_NOT_STARTED
        } else if (it.endCfp.time > timeAndDateUtils.currentTimeInMilliseconds) {
            CfpStatus.CFP_STATUS_IN_PROGRESS
        } else {
            CfpStatus.CFP_STATUS_ENDED
        }
        ConferenceDetailsData(
                id = it.id,
                name = it.name,
                location = it.location,
                startDate = it.startDate,
                endDate = it.endDate,
                cfpStatus = cfpStatus
        )
    }
    fun handle() : DataSource<List<ConferenceDetailsData>> {
        taskRunner.run {
            dataSource.setValue(storageClient.getConferencesDetailsList().toConferenceDetailsData())
            val conferences = networkClient.getConferencesList()
            conferences.forEach { conference ->
                val details = networkClient.getConferenceDetails(conference.conferenceId)
                details?.let {
                    val conferenceDetails = ConferenceDetails(
                            id = conference.conferenceId,
                            name = details.name,
                            location = details.location,
                            startDate = details.startDate,
                            endDate = details.endDate,
                            startCfp = details.startCfp,
                            endCfp = details.endCfp
                    )
                    storageClient.addConferenceDetails(conferenceDetails)
                    dataSource.setValue(storageClient.getConferencesDetailsList().toConferenceDetailsData())
                }
            }
        }
        return dataSource
    }
}

class DataSource<T>(
        private val taskRunner: TaskRunner
) {
    private val channel = BroadcastChannel<T>(Channel.CONFLATED)
    fun notifyOnChange(observer: (T) -> Unit) {
        val subscription = channel.openSubscription()
        taskRunner.run {
            subscription.consumeEach {
                observer(it)
            }
        }
    }

    fun setValue(value: T) {
        channel.offer(value)
    }
}