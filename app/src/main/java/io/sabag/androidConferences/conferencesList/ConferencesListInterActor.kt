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
    override fun observeConferences(observer: (List<ConferenceDetailsData>) -> Unit) {
        val useCase = UseCase()
        useCase.networkClient = networkClient
        useCase.storageClient = storageClient
        useCase.taskRunner = taskRunner
        val dataSource = useCase.handle()
        dataSource.notifyOnChange {
            conferenceDetailsList ->
            val list = conferenceDetailsList.filter { it.startDate.time > timeAndDateUtils.currentTimeInMilliseconds }
            observer(list.sortedBy { it.startDate.time })
        }
    }
}