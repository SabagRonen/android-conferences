package io.sabag.androidConferences.network

import io.sabag.androidConferences.Conference
import io.sabag.androidConferences.pluginInterfaces.IConferencesNetworkClient
import io.sabag.androidConferences.network.ConferenceDetailsMatcher.Companion.matchConferenceDetails
import io.sabag.androidConferences.TestApi
import io.sabag.androidConferences.ConferenceDetails as Details
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class ConferencesNetworkClientTests {

    @get:Rule val webServer: MockWebServer by lazy {
        val server = MockWebServer()
        baseUrl = server.url("").toString()
        server.setDispatcher(EnforcePathDispatcher)
        server
    }

    private lateinit var client: IConferencesNetworkClient
    private lateinit var baseUrl: String
    private val testApi = TestApi()

    @Before
    fun setupTest() {
        client = ConferencesNetworkClient(baseUrl)
        EnforcePathDispatcher.clearResponseQueue()
    }

    @Test
    fun whenPathIsInvalidShouldGetEmptyConferencesList() {
        // prepare
        setResponseForPath("invalid/path", "conferences_response.json")

        // act
        val conferencesList = client.getConferencesList()

        // verify
        assertEquals(emptyList<Conference>(), conferencesList)
    }

    @Test
    fun whenPathIsValidShouldGetConferencesList() {
        // prepare
        val expected =
                Conference("https://raw.githubusercontent.com/AndroidStudyGroup/conferences/gh-pages/_conferences/devfest-twin-cities-2017.md")
        setResponseForPath("/_conferences?ref=gh-pages", "conferences_response.json")

        // act
        val conferencesList = client.getConferencesList()

        // verify
        assertEquals(266, conferencesList.size)
        assertEquals(expected, conferencesList[123])
    }

    @Test
    fun whenRequestEndWithTimeOutShouldReturnEmptyList() {
        // act
        val conferencesList = client.getConferencesList()

        // verify
        assertEquals(emptyList<Conference>(), conferencesList)
    }

    @Test
    fun whenRequestForNotExistConferenceDetailsShouldGetNull() {
        // prepare
        setResponseForPath("invalid/path", "conference_details_with_cfp")

        // act
        val details = client.getConferenceDetails("id")

        // verify
        assertNull(details)
    }

    @Test
    fun whenPathIsValidShouldGetConferencesDetails() {
        // prepare
        val expected = Details(
                id = "http://localhost:49603/AndroidStudyGroup/conferences/gh-pages/_conferences/360%7Candev-2016.md",
                name = "360|AnDev",
                location = "Denver, Colorado, USA",
                startDate = testApi.getDateFromYearMonthDay(2016, Calendar.JULY, 28),
                endDate = testApi.getDateFromYearMonthDay(2016, Calendar.JULY, 29)
        )
        setResponseForPath(
                "/AndroidStudyGroup/conferences/gh-pages/_conferences/360%7Candev-2016.md",
                "conference_details_with_cfp"
        )

        // act
        val url = "${baseUrl}AndroidStudyGroup/conferences/gh-pages/_conferences/360%7Candev-2016.md"
        val response = client.getConferenceDetails(url)

        // verify
        assertThat(expected, matchConferenceDetails(response!!))
    }

    private fun setResponseForPath(path: String, filePath: String) {
        val json = testApi.getFileContent(filePath)
        EnforcePathDispatcher.addExpectedPath(path)
        val mockResponse = MockResponse()
        mockResponse.setBody(json)
        webServer.enqueue(mockResponse)
    }
}