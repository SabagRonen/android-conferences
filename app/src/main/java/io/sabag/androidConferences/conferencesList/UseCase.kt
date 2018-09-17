package io.sabag.androidConferences.conferencesList

import io.sabag.androidConferences.ConferenceDetails
import io.sabag.androidConferences.pluginInterfaces.IConferencesNetworkClient
import io.sabag.androidConferences.pluginInterfaces.IConferencesStorageClient
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach

class UseCase {
    lateinit var networkClient: IConferencesNetworkClient
    lateinit var storageClient: IConferencesStorageClient
    lateinit var taskRunner: TaskRunner
    private val dataSource: DataSource<List<ConferenceDetails>> by lazy {
        DataSource<List<ConferenceDetails>>(taskRunner)
    }

    fun handle() : DataSource<List<ConferenceDetails>> {
        taskRunner.run {
            dataSource.setValue(storageClient.getConferencesDetailsList())
            val conferences = networkClient.getConferencesList()
            conferences.forEach { conference ->
                val details = networkClient.getConferenceDetails(conference.conferenceId)
                details?.let {
                    val conferenceDetails = ConferenceDetails(
                            id = conference.conferenceId,
                            name = details.name,
                            location = details.location,
                            startDate = details.startDate,
                            endDate = details.endDate
                    )
                    storageClient.addConferenceDetails(conferenceDetails)
                    dataSource.setValue(storageClient.getConferencesDetailsList())
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