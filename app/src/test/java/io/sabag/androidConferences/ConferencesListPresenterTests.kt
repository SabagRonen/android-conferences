package io.sabag.androidConferences

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import io.sabag.androidConferences.conferencesList.ConferencesListPresenter
import io.sabag.androidConferences.conferencesList.IConferencesListInterActor
import io.sabag.androidConferences.pluginInterfaces.ITimeAndDateUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.*
import org.mockito.Mockito.`when` as mockitoWhen

import java.util.*

class ConferencesListPresenterTests {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var interActor: IConferencesListInterActor
    private lateinit var presenter: ConferencesListPresenter
    private lateinit var timeAndDateUtils: ITimeAndDateUtils
    private lateinit var conferenceDetailsListObserver: (List<ConferenceDetails>) -> Unit
    private lateinit var conferenceStateListListener: (List<ConferenceState>) -> Unit

    companion object {
        private val START_DATE = Date(1535997553432)
        private val END_DATE = Date(1536077553432)
    }

    private val lifecycle : Lifecycle by lazy {
        val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        lifecycle
    }

    private val details = ConferenceDetails(
            id = "",
            name = "",
            location = "",
            startDate = START_DATE,
            endDate = null
    )

    private val state = ConferenceState(
            title = "",
            location = "",
            dates = "2018-09-03"
    )

    @Before
    fun setupTest() {
        timeAndDateUtils = TimeAndDateUtils()
        interActor = mock(IConferencesListInterActor::class.java)
        presenter = ConferencesListPresenter(interActor, timeAndDateUtils)
        presenter.loadData()
        conferenceDetailsListObserver = getConferencesDetailsObserver()
        conferenceStateListListener = getConferenceStateListListener()
    }

    @Test
    fun whenLoadDataCalledShouldCallToInterActorObserveConferences() {
        // verify
        verify(interActor).observeConferences(anyNonNull())
    }

    @Test
    fun whenObserverConferenceDetailsShouldSendStateWithTitleAsTheName() {
        // act
        conferenceDetailsListObserver(listOf(details.copy(name = "tests are fun")))

        // verify
        verify(conferenceStateListListener).invoke(listOf(state.copy(title = "tests are fun")))
    }

    @Test
    fun whenObserverConferenceDetailsShouldSendStateWithLocationAsTheLocation() {
        // act
        conferenceDetailsListObserver(listOf(details.copy(location = "tests are fun")))

        // verify
        verify(conferenceStateListListener).invoke(listOf(state.copy(location = "tests are fun")))
    }

    @Test
    fun whenObserverConferenceDetailsWithoutEndDateShouldSendStateWithDatesAsTheStartDate() {
        // act
        conferenceDetailsListObserver(listOf(details.copy(startDate = START_DATE, endDate = null)))

        // verify
        verify(conferenceStateListListener).invoke(listOf(state.copy(dates = "2018-09-03")))
    }

    @Test
    fun whenObserverConferenceDetailsWithEndDateShouldSendStateWithDatesAsTheStartDateAndEndDate() {
        // act
        conferenceDetailsListObserver(listOf(details.copy(startDate = START_DATE, endDate = END_DATE)))

        // verify
        verify(conferenceStateListListener).invoke(listOf(state.copy(dates = "2018-09-03 - 2018-09-04")))
    }

    private fun getConferenceStateListListener(): (List<ConferenceState>) -> Unit {
        val listener = lambdaMock<(List<ConferenceState>) -> Unit>()
        presenter.observeConferencesState(lifecycle, listener)
        return listener
    }

    private fun getConferencesDetailsObserver(): (List<ConferenceDetails>) -> Unit {
        val captor = lambdaArgumentCaptor<(List<ConferenceDetails>) -> Unit>()
        verify(interActor).observeConferences(cap(captor))
        return captor.value
    }
}