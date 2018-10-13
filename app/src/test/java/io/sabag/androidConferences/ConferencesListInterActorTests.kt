package io.sabag.androidConferences

import io.sabag.androidConferences.conferencesList.CfpStatus
import io.sabag.androidConferences.conferencesList.ConferenceDetailsData
import io.sabag.androidConferences.network.ConferenceDetails as NetworkDetails
import io.sabag.androidConferences.network.ConferencesNetworkClient
import io.sabag.androidConferences.network.Content
import io.sabag.androidConferences.network.EnforcePathDispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import io.sabag.androidConferences.conferencesList.ConferencesListInterActor
import io.sabag.androidConferences.conferencesList.TaskRunner
import io.sabag.androidConferences.network.ConferenceDetailsConverter
import io.sabag.androidConferences.pluginInterfaces.IConferencesNetworkClient
import io.sabag.androidConferences.pluginInterfaces.IConferencesStorageClient
import io.sabag.androidConferences.pluginInterfaces.ITimeAndDateUtils
import io.sabag.androidConferences.storage.FakeConferencesStorageClient
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class TestTaskRunner : TaskRunner, CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = job

    private lateinit var job: Job

    override fun run(block: suspend () -> Unit) {
        runBlocking {
            job = launch {
                block()
            }
        }
    }

    override fun cancel() {
        if (job.isActive) {
            job.cancel()
        }
    }

}

class ConferencesListInterActorTests {

    @get:Rule
    val webServer: MockWebServer by lazy {
        val server = MockWebServer()
        baseUrl = server.url("").toString()
        server.setDispatcher(EnforcePathDispatcher)
        server
    }

    private lateinit var taskRunner: TestTaskRunner
    private val baseDate = Date()
    private val networkDetails = NetworkDetails(
            name = null,
            website = null,
            location = null,
            dateStart = null,
            dateEnd = null,
            cfpStart = null,
            cfpEnd = null,
            cfpSite = null
    )
    private val conferenceDetails = ConferenceDetailsData(
            id = "",
            name = "",
            location = "",
            startDate = getDateByDaysOffset(baseDate, 0),
            endDate = null,
            cfpStatus = CfpStatus.CFP_STATUS_NA
    )

    private val testApi = TestApi()
    private lateinit var networkClient: IConferencesNetworkClient
    private lateinit var storageClient: IConferencesStorageClient
    private lateinit var baseUrl: String
    private lateinit var interActor: ConferencesListInterActor
    private lateinit var timeAndDateUtils: ITimeAndDateUtils
    private var response: List<ConferenceDetailsData>? = null

    @Before
    fun setupTest() {
        networkClient = ConferencesNetworkClient(baseUrl)
        storageClient = FakeConferencesStorageClient()
        timeAndDateUtils = TimeAndDateUtils()
        EnforcePathDispatcher.clearResponseQueue()
        taskRunner = TestTaskRunner()
        interActor = ConferencesListInterActor(
                networkClient,
                storageClient,
                timeAndDateUtils,
                taskRunner
        )
        response = null
    }

    @Test
    fun whenConferencesListIsEmptyShouldSendEmpyDetailsList() {
        interActor.observeConferences {
            response = it
            taskRunner.cancel()
        }
        // verify
        assertEquals(emptyList<ConferenceDetails>(), response)
    }

    @Test
    fun whenServerHaveConferencesDetailsShouldReturnTheDetails() {
        // prepare
        addContentResponse(
                path = "/_conferences?ref=gh-pages" ,
                contentList = listOf("path/to/url")
        )
        addConferenceDetailsResponse(
                getDateByDaysOffset(baseDate, 2),
                getDateByDaysOffset(baseDate, 3),
                "/path/to/url"
        )

        // act
        interActor.observeConferences {
            response = it
            taskRunner.cancel()
        }

        // verify
        val expected = listOf(
                conferenceDetails.copy(
                        id = "path/to/url",
                        startDate = getDateByDaysOffset(baseDate, 2),
                        endDate = getDateByDaysOffset(baseDate, 3)
                )
        )
        assertEquals(expected, response)
    }

    @Test
    fun whenObserveConferencesAfterAlreadyObservedAndNetworkReturnEmptyListShouldReturnPreviousList() {
        // prepare
        addContentResponse(
                path = "/_conferences?ref=gh-pages" ,
                contentList = listOf("path/to/url")
        )
        addConferenceDetailsResponse(
                getDateByDaysOffset(baseDate, 2),
                getDateByDaysOffset(baseDate, 3),
                "/path/to/url"
        )
        interActor.observeConferences {
            taskRunner.cancel()
        }

        // act
        interActor.observeConferences {
            response = it
            taskRunner.cancel()
        }

        // verify
        val expected = listOf(
                conferenceDetails.copy(
                        id = "path/to/url",
                        startDate = getDateByDaysOffset(baseDate, 2),
                        endDate = getDateByDaysOffset(baseDate, 3)
                )
        )
        assertEquals(expected, response)
    }

    @Test
    fun whenObserveConferencesShouldNotReturnPastConferences() {
        // prepare
        addContentResponse(
                path = "/_conferences?ref=gh-pages" ,
                contentList = listOf("path/to/url")
        )
        addConferenceDetailsResponse(
                getDateByDaysOffset(baseDate, -3),
                getDateByDaysOffset(baseDate, -2),
                "/path/to/url"
        )

        // act
        interActor.observeConferences {
            response = it
            taskRunner.cancel()
        }

        // verify
        assertEquals(emptyList<ConferenceDetails>(), response)
    }

    @Test
    fun whenObserveOutOfOrderConferencesShouldGetThemInOrder() {
        // prepare
        addContentResponse(
                path = "/_conferences?ref=gh-pages" ,
                contentList = listOf("path/to/url1", "path/to/url2")
        )
        addConferenceDetailsResponse(
                getDateByDaysOffset(baseDate, 5),
                getDateByDaysOffset(baseDate, 6),
                "/path/to/url1"
        )
        addConferenceDetailsResponse(
                getDateByDaysOffset(baseDate, 2),
                getDateByDaysOffset(baseDate, 3),
                "/path/to/url2"
        )

        // act
        interActor.observeConferences {
            response = it
            taskRunner.cancel()
        }

        // verify
        val expected = listOf(
                conferenceDetails.copy(
                        id = "path/to/url2",
                        startDate = getDateByDaysOffset(baseDate, 2),
                        endDate = getDateByDaysOffset(baseDate, 3)
                ),
                conferenceDetails.copy(
                        id = "path/to/url1",
                        startDate = getDateByDaysOffset(baseDate, 5),
                        endDate = getDateByDaysOffset(baseDate, 6)
                )
        )
        assertEquals(expected, response)
    }

    @Test
    fun whenObservedDetailsWithNullCfpEndShouldReturnDetailsDataWithStatusNa() {
        // prepare
        addContentResponse(
                path = "/_conferences?ref=gh-pages" ,
                contentList = listOf("path/to/url")
        )
        addConferenceDetailsResponse(
                getDateByDaysOffset(baseDate, 20),
                getDateByDaysOffset(baseDate, 21),
                "/path/to/url",
                cfpStart = getDateByDaysOffset(baseDate, 3),
                cfpEnd = null
        )

        // act
        interActor.observeConferences {
            response = it
            taskRunner.cancel()
        }

        // verify
        val expected = listOf(
                conferenceDetails.copy(
                        id = "path/to/url",
                        startDate = getDateByDaysOffset(baseDate, 20),
                        endDate = getDateByDaysOffset(baseDate, 21),
                        cfpStatus = CfpStatus.CFP_STATUS_NA
                )
        )
        assertEquals(expected, response)
    }

    @Test
    fun whenObservedDetailsWithNullCfpStartShouldReturnDetailsDataWithStatusNa() {
        // prepare
        addContentResponse(
                path = "/_conferences?ref=gh-pages" ,
                contentList = listOf("path/to/url")
        )
        addConferenceDetailsResponse(
                getDateByDaysOffset(baseDate, 20),
                getDateByDaysOffset(baseDate, 21),
                "/path/to/url",
                cfpStart = null,
                cfpEnd = getDateByDaysOffset(baseDate, 3)
        )

        // act
        interActor.observeConferences {
            response = it
            taskRunner.cancel()
        }

        // verify
        val expected = listOf(
                conferenceDetails.copy(
                        id = "path/to/url",
                        startDate = getDateByDaysOffset(baseDate, 20),
                        endDate = getDateByDaysOffset(baseDate, 21),
                        cfpStatus = CfpStatus.CFP_STATUS_NA
                )
        )
        assertEquals(expected, response)
    }

    @Test
    fun whenObservedDetailsWithCfpThatNotStartedShouldReturnDetailsDataWithStatusNotStarted() {
        // prepare
        addContentResponse(
                path = "/_conferences?ref=gh-pages" ,
                contentList = listOf("path/to/url")
        )
        addConferenceDetailsResponse(
                getDateByDaysOffset(baseDate, 20),
                getDateByDaysOffset(baseDate, 21),
                "/path/to/url",
                cfpStart = getDateByDaysOffset(baseDate, 1),
                cfpEnd = getDateByDaysOffset(baseDate, 3)
        )

        // act
        interActor.observeConferences {
            response = it
            taskRunner.cancel()
        }

        // verify
        val expected = listOf(
                conferenceDetails.copy(
                        id = "path/to/url",
                        startDate = getDateByDaysOffset(baseDate, 20),
                        endDate = getDateByDaysOffset(baseDate, 21),
                        cfpStatus = CfpStatus.CFP_STATUS_NOT_STARTED
                )
        )
        assertEquals(expected, response)
    }

    @Test
    fun whenObservedDetailsWithCfpThatInProgressShouldReturnDetailsDataWithStatusInProgress() {
        // prepare
        addContentResponse(
                path = "/_conferences?ref=gh-pages" ,
                contentList = listOf("path/to/url")
        )
        addConferenceDetailsResponse(
                getDateByDaysOffset(baseDate, 20),
                getDateByDaysOffset(baseDate, 21),
                "/path/to/url",
                cfpStart = getDateByDaysOffset(baseDate, -1),
                cfpEnd = getDateByDaysOffset(baseDate, 3)
        )

        // act
        interActor.observeConferences {
            response = it
            taskRunner.cancel()
        }

        // verify
        val expected = listOf(
                conferenceDetails.copy(
                        id = "path/to/url",
                        startDate = getDateByDaysOffset(baseDate, 20),
                        endDate = getDateByDaysOffset(baseDate, 21),
                        cfpStatus = CfpStatus.CFP_STATUS_IN_PROGRESS
                )
        )
        assertEquals(expected, response)
    }

    @Test
    fun whenObservedDetailsWithCfpThatEndedShouldReturnDetailsDataWithStatusEnded() {
        // prepare
        addContentResponse(
                path = "/_conferences?ref=gh-pages" ,
                contentList = listOf("path/to/url")
        )
        addConferenceDetailsResponse(
                getDateByDaysOffset(baseDate, 20),
                getDateByDaysOffset(baseDate, 21),
                "/path/to/url",
                cfpStart = getDateByDaysOffset(baseDate, -3),
                cfpEnd = getDateByDaysOffset(baseDate, -1)
        )

        // act
        interActor.observeConferences {
            response = it
            taskRunner.cancel()
        }

        // verify
        val expected = listOf(
                conferenceDetails.copy(
                        id = "path/to/url",
                        startDate = getDateByDaysOffset(baseDate, 20),
                        endDate = getDateByDaysOffset(baseDate, 21),
                        cfpStatus = CfpStatus.CFP_STATUS_ENDED
                )
        )
        assertEquals(expected, response)
    }

    private fun getDateByDaysOffset(baseDate: Date, offsetInDays: Int): Date {
        val dayInMilliseconds = 1000 * 60 * 60 * 24
        val calendar = Calendar.getInstance()
        calendar.time = Date(baseDate.time + offsetInDays * dayInMilliseconds)
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun addResponse(path: String, body: String) {
        EnforcePathDispatcher.addExpectedPath(path)
        val mockResponse = MockResponse()
        mockResponse.setBody(body)
        webServer.enqueue(mockResponse)
    }

    private fun addContentResponse(path: String, contentList: List<String>) {
        EnforcePathDispatcher.addExpectedPath(path)
        val mockResponse = MockResponse()
        mockResponse.setBody(testApi.toJson(contentList.map { Content(it) }))
        webServer.enqueue(mockResponse)
    }

    private fun addConferenceDetailsResponse(
            dateStart: Date,
            dateEnd: Date,
            path: String,
            cfpStart: Date? = null,
            cfpEnd: Date? = null) {
        val details = networkDetails.copy(
                dateStart = dateStart,
                dateEnd = dateEnd,
                cfpStart = cfpStart,
                cfpEnd = cfpEnd
        )
        val body = ConferenceDetailsConverter().convertToString(details)
        addResponse(
                path = path,
                body = body
        )
    }
}
