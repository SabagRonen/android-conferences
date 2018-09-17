package io.sabag.androidConferences.storage

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import io.sabag.androidConferences.pluginInterfaces.IConferencesStorageClient
import org.junit.After

class RoomConferencesListStorageTests : IConferencesListStorageTests() {
    private lateinit var room: ConferencesDatabase

    @After
    fun cleanResources() {
        room.close()
    }

    override fun getActualConferencesStorageClient() : IConferencesStorageClient {
        val context = InstrumentationRegistry.getTargetContext().applicationContext
        room = Room.inMemoryDatabaseBuilder(context, ConferencesDatabase::class.java).build()
        return RoomConferencesStorageClient(room.conferencesDao(), room.conferenceDetailsDao())
    }
}