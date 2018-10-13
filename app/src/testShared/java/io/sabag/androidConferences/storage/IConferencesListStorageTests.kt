package io.sabag.androidConferences.storage

import io.sabag.androidConferences.Conference
import io.sabag.androidConferences.ConferenceDetails
import io.sabag.androidConferences.pluginInterfaces.IConferencesStorageClient
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

abstract class IConferencesListStorageTests {

    private lateinit var storageClient: IConferencesStorageClient

    @Before
    fun setupTest() {
        storageClient = getActualConferencesStorageClient()
    }

    abstract fun getActualConferencesStorageClient() : IConferencesStorageClient

    @Test
    fun whenGetConferencesBeforeAddingListShouldReturnEmptyList() {
        // act
        val conferences = storageClient.getConferences()

        // verify
        assertEquals(emptyList<List<Conference>>(), conferences)
    }

    @Test
    fun whenGetConferencesAfterAddingListShouldReturnAddedList() {
        // prepare
        val conferencesList = listOf(Conference("id1"), Conference("id2"))
        storageClient.addConferences(conferencesList)

        // act
        val conferences = storageClient.getConferences()

        // verify
        assertEquals(conferencesList, conferences)
    }

    @Test
    fun whenAddConferencesMoreThanOnceAndAllConferencesUniqueShouldAddAllConferences() {
        // prepare
        val conferencesList1 = listOf(Conference("id1"), Conference("id2"))
        storageClient.addConferences(conferencesList1)
        val conferencesList2 = listOf(Conference("id3"), Conference("id4"))
        storageClient.addConferences(conferencesList2)

        // act
        val conferences = storageClient.getConferences()

        // verify
        val expected = listOf(conferencesList1, conferencesList2).flatten()
        assertEquals(expected, conferences)
    }

    @Test
    fun whenAddConferencesThatHaveAlreadyBeingAddedShouldAddOnlyUniqueConferences() {
        // prepare
        val conferencesList1 = listOf(Conference("id1"), Conference("id2"))
        storageClient.addConferences(conferencesList1)
        val conferencesList2 = listOf(Conference("id1"), Conference("id3"))
        storageClient.addConferences(conferencesList2)

        // act
        val conferences = storageClient.getConferences()

        // verify
        val expected = listOf(Conference("id1"), Conference("id2"), Conference("id3"))
        assertEquals(expected, conferences)
    }

    @Test
    fun whenAddDetailsWithExistIdShouldReplaceOldDetails() {
        // prepare
        val conferenceDetails = ConferenceDetails(
                id = "id1",
                name = "name1",
                location = "location",
                startDate = Date(),
                endDate = null,
                startCfp = null,
                endCfp = null
        )
        storageClient.addConferenceDetails(conferenceDetails)
        storageClient.addConferenceDetails(conferenceDetails.copy(name = "name2"))

        // act
        val actual = storageClient.getConferencesDetailsList()

        // verify
        assertEquals(listOf(conferenceDetails.copy(name = "name2")), actual)
    }

    @Test
    fun whenGetConferencesDetailsListAndNotDetailsWasAddedShouldReturnEmptyList() {
        // act
        val actual = storageClient.getConferencesDetailsList()

        // verify
        assertEquals(emptyList<ConferenceDetails>(), actual)
    }
}