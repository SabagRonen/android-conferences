package io.sabag.androidConferences.network

import io.sabag.androidConferences.ConferenceDetails
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import java.net.URI

class ConferenceDetailsMatcher(
        private val details: ConferenceDetails
) : TypeSafeMatcher<ConferenceDetails>() {

    private var cause: String? = null

    companion object {
        fun matchConferenceDetails(details: ConferenceDetails) = ConferenceDetailsMatcher(details)
    }

    override fun matchesSafely(item: ConferenceDetails?): Boolean {
        if (item == null) return false

        if (URI.create(item.id).path != URI.create(details.id).path) {
            cause = "with id (path only without host) [${URI.create(details.id).path}]"
            return false
        }

        if (item.name != details.name) {
            cause = "with name [${details.name}]"
            return false
        }

        if (item.location != details.location) {
            cause = "with location [${details.location}]"
            return false
        }

        if (item.startDate != details.startDate) {
            cause = "with startDate [${details.startDate}]"
            return false
        }

        if (item.endDate != details.endDate) {
            cause = "with endDate [${details.endDate}]"
            return false
        }

        if (item.startCfp != details.startCfp) {
            cause = "with startCfp [${details.startCfp}]"
            return false
        }

        if (item.endCfp != details.endCfp) {
            cause = "with endCfp [${details.endCfp}]"
            return false
        }

        return true
    }

    override fun describeTo(description: Description?) {
        description?.appendText("Conference details ${cause?.let { ", $it" } ?: ""}")
    }

}