package io.sabag.androidConferences.conferencesList

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.runner.AndroidJUnit4
import io.sabag.androidConferences.*
import io.sabag.androidConferences.matchers.BackgroundResourceMatcher.Companion.withBackgroundResource
import org.hamcrest.Matchers.not
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class ConferencesListViewTests {
    @get:Rule val activityTestRule = createFakeFragmentInjector<ConferencesListFragment> {
        presenter = mockPresenter
    }
    private val mockPresenter = mock(IConferencesListPresenter::class.java)
    private lateinit var conferenceStateListObserver: (List<ConferenceState>) -> Unit
    private val conferenceState = ConferenceState(
            title = "",
            location = "",
            info = "",
            extraInfo = null
    )
    private val extraInfo = ExtraInfo(
            text = R.string.cfp,
            icon = null
    )

    @Before
    fun setupTest() {
        conferenceStateListObserver = getConferenceStateListObserver()
    }

    @Test
    fun presenterLoadDataShouldCalledWhenViewStart() {
        // verify
        verify(mockPresenter).loadData()
    }

    @Test
    fun whenObserveConferenceStateShouldShowTheNameAsTitle() {
        // act
        changeUi{
            conferenceStateListObserver(listOf(conferenceState.copy(title = "cool conference")))
        }

        // verify
        onView(withId(R.id.title)).check(matches(withText("cool conference")))
    }

    @Test
    fun whenObserveConferenceStateShouldShowTheLocationAsSubtitle() {
        // act
        changeUi{
            conferenceStateListObserver(listOf(conferenceState.copy(location = "127.0.0.1")))
        }

        // verify
        onView(withId(R.id.subtitle)).check(matches(withText("127.0.0.1")))
    }

    @Test
    fun whenObserveConferenceStateShouldShowTheStartDateAsStartDate() {
        // act
        changeUi{
            conferenceStateListObserver(listOf(conferenceState.copy(info = "03.09.2018 - 04.09.2018")))
        }

        // verify
        onView(withId(R.id.info)).check(matches(withText("03.09.2018 - 04.09.2018")))
    }

    @Test
    fun whenObserveConferenceStateWithExtraInfoShouldShowExtraTextView() {
        // act
        changeUi{
            conferenceStateListObserver(listOf(conferenceState.copy(extraInfo = extraInfo)))
        }

        // verify
        onView(withId(R.id.extraText)).check(matches(isDisplayed()))
    }

    @Test
    fun whenObserveConferenceStateWithoutExtraInfoShouldHideExtraInfoView() {
        // prepare
        changeUi{
            conferenceStateListObserver(listOf(conferenceState.copy(extraInfo = extraInfo)))
        }

        // act
        changeUi{
            conferenceStateListObserver(listOf(conferenceState.copy(extraInfo = null)))
        }

        // verify
        onView(withId(R.id.extraText)).check(matches(not(isDisplayed())))
    }

    @Test
    fun whenObserveConferenceStateWithExtraInfoShouldShowExtraInfoText() {
        // prepare
        val info = extraInfo.copy(text = R.string.cfp)

        // act
        changeUi{
            conferenceStateListObserver(listOf(conferenceState.copy(extraInfo = info)))
        }

        // verify
        onView(withId(R.id.extraText)).check(matches(withText("CFP")))
    }

    @Test
    fun whenObserveConferenceStateWithExtraInfoWithIconShouldShowExtraInfoIcon() {
        // prepare
        val info = extraInfo.copy(icon = R.drawable.status_ended)

        // act
        changeUi{
            conferenceStateListObserver(listOf(conferenceState.copy(extraInfo = info)))
        }

        // verify
        onView(withId(R.id.extraStatus)).check(matches(withBackgroundResource(R.drawable.status_ended)))
    }

    @Test
    fun whenClickOnPastButtonShouldDelegateToPresenter() {
        onView(withId(R.id.pastButton)).perform(click())

        verify(mockPresenter).pastButtonClicked()
    }

    @Test
    fun whenClickOnUpcomingButtonShouldDelegateToPresenter() {
        onView(withId(R.id.upcomingButton)).perform(click())

        verify(mockPresenter).upcomingButtonClicked()
    }

    @Test
    fun whenClickOnMoreButtonShouldDelegateToPresenter() {
        onView(withId(R.id.moreButton)).perform(click())

        verify(mockPresenter).moreButtonClicked()
    }

    private fun getConferenceStateListObserver() : (List<ConferenceState>) -> Unit {
        val captor = lambdaArgumentCaptor<(List<ConferenceState>) -> Unit>()
        verify(mockPresenter).observeConferencesState(anyNonNull(), cap(captor))
        return captor.value
    }

    private fun changeUi(block: () -> Unit) {
        activityTestRule.activity.runOnUiThread {
            block()
        }
        Espresso.onIdle()
    }
}