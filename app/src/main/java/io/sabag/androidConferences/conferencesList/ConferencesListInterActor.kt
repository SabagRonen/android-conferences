package io.sabag.androidConferences.conferencesList

import io.sabag.androidConferences.pluginInterfaces.IConferencesNetworkClient
import io.sabag.androidConferences.pluginInterfaces.IConferencesStorageClient
import io.sabag.androidConferences.pluginInterfaces.ITimeAndDateUtils

class ConferencesListInterActor(
        private val networkClient: IConferencesNetworkClient,
        private val storageClient: IConferencesStorageClient,
        private val timeAndDateUtils: ITimeAndDateUtils,
        private val taskRunner: TaskRunner
) : IConferencesListInterActor {
    override fun observeConferences(shouldObserveFuture: Boolean, observer: (List<ConferenceDetailsData>) -> Unit) {
        val useCase = UseCase()
        useCase.networkClient = networkClient
        useCase.storageClient = storageClient
        useCase.taskRunner = taskRunner
        useCase.timeAndDateUtils = timeAndDateUtils
        val dataSource = useCase.handle()
        val filterCriteria: (ConferenceDetailsData) -> Boolean = { data ->
            if (shouldObserveFuture)
                data.startDate.time > timeAndDateUtils.currentTimeInMilliseconds
            else
                data.startDate.time <= timeAndDateUtils.currentTimeInMilliseconds
        }
        dataSource.notifyOnChange {
            conferenceDetailsList ->
            val list = conferenceDetailsList.filter(filterCriteria)
            observer(list.sortedBy { it.startDate.time })
        }
    }
}