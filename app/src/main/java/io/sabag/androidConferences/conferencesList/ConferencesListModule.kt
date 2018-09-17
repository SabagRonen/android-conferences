package io.sabag.androidConferences.conferencesList

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.sabag.androidConferences.storage.RoomConferencesStorageClient
import io.sabag.androidConferences.TimeAndDateUtils
import io.sabag.androidConferences.network.ConferencesNetworkClient
import io.sabag.androidConferences.storage.ConferencesDatabase

@Module
abstract class ConferencesListModule {
    @ContributesAndroidInjector(modules = [ConferencesListFragmentModule::class])
    abstract fun bindConferencesListFragment() : ConferencesListFragment

    @Module
    class ConferencesListFragmentModule {
        @Provides
        fun providePresenter(
                app: Application
        ) : IConferencesListPresenter {
            val networkClient = ConferencesNetworkClient()
            val room = Room.databaseBuilder(
                    app.applicationContext,
                    ConferencesDatabase::class.java,
                    "conferencesDatabase").build()
            val storageClient = RoomConferencesStorageClient(
                    room.conferencesDao(),
                    room.conferenceDetailsDao()
            )
            val timeAndDateUtils = TimeAndDateUtils()
            val taskRunner = LaunchTaskRunner()
            val interActor = ConferencesListInterActor(
                    networkClient,
                    storageClient,
                    timeAndDateUtils,
                    taskRunner
            )
            return ConferencesListPresenter(interActor, TimeAndDateUtils())
        }
    }
}