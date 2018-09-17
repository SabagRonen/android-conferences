package io.sabag.androidConferences.network

import io.sabag.androidConferences.TestApi
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class ConferenceDetailsConverterTest {

    private val testApi = TestApi()
    private val converter = ConferenceDetailsConverter()
    private val conferenceDetailsWithCfp by lazy {
        testApi.getFileContent("conference_details_with_cfp")
    }
    private val conferenceDetailsWithoutCfp by lazy {
        testApi.getFileContent("conference_details_without_cfp")
    }

    @Test
    fun toConferenceDetailsShouldParseNameCorrectly() {
        // act
        val conferenceDetails = converter.convertToConferenceDetails(conferenceDetailsWithCfp)

        // verify
        assertEquals("360|AnDev", conferenceDetails?.name)
    }

    @Test
    fun toConferenceDetailsShouldParseWebsiteCorrectly() {
        // act
        val conferenceDetails = converter.convertToConferenceDetails(conferenceDetailsWithCfp)

        // verify
        assertEquals("http://360andev.com/", conferenceDetails?.website)
    }

    @Test
    fun toConferenceDetailsShouldParseLocationCorrectly() {
        // act
        val conferenceDetails = converter.convertToConferenceDetails(conferenceDetailsWithCfp)

        // verify
        assertEquals("Denver, Colorado, USA", conferenceDetails?.location)
    }

    @Test
    fun toConferenceDetailsShouldParseDateStartCorrectly() {
        // act
        val conferenceDetails = converter.convertToConferenceDetails(conferenceDetailsWithCfp)

        // verify
        assertEquals(
                testApi.getDateFromYearMonthDay( 2016, Calendar.JULY, 28),
                conferenceDetails?.dateStart
        )
    }

    @Test
    fun toConferenceDetailsShouldParseDateEndCorrectly() {
        // act
        val conferenceDetails = converter.convertToConferenceDetails(conferenceDetailsWithCfp)

        // verify
        assertEquals(
                testApi.getDateFromYearMonthDay( 2016, Calendar.JULY, 29),
                conferenceDetails?.dateEnd
        )
    }

    @Test
    fun toConferenceDetailsWithCfpShouldParseCfpStartCorrectly() {
        // act
        val conferenceDetails = converter.convertToConferenceDetails(conferenceDetailsWithCfp)

        // verify
        assertEquals(
                testApi.getDateFromYearMonthDay( 2016, Calendar.JANUARY, 27),
                conferenceDetails?.cfpStart
        )
    }

    @Test
    fun toConferenceDetailsWithoutCfpShouldReturnStartCfpNull() {
        // act
        val conferenceDetails = converter.convertToConferenceDetails(conferenceDetailsWithoutCfp)

        // verify
        assertNull(conferenceDetails?.cfpStart)
    }

    @Test
    fun toConferenceDetailsWithCfpShouldParseCfpEndCorrectly() {
        // act
        val conferenceDetails = converter.convertToConferenceDetails(conferenceDetailsWithCfp)

        // verify
        assertEquals(
                testApi.getDateFromYearMonthDay( 2016, Calendar.APRIL, 29),
                conferenceDetails?.cfpEnd
        )
    }

    @Test
    fun toConferenceDetailsWithoutCfpShouldReturnEndCfpNull() {
        // act
        val conferenceDetails = converter.convertToConferenceDetails(conferenceDetailsWithoutCfp)

        // verify
        assertNull(conferenceDetails?.cfpEnd)
    }

    @Test
    fun toConferenceDetailsWithCfpShouldParseCfpSiteCorrectly() {
        // act
        val conferenceDetails = converter.convertToConferenceDetails(conferenceDetailsWithCfp)

        // verify
        assertEquals("http://360andev.com/call-for-papers/", conferenceDetails?.cfpSite)
    }

    @Test
    fun toConferenceDetailsWithoutCfpShouldReturnCfpSiteNull() {
        // act
        val conferenceDetails = converter.convertToConferenceDetails(conferenceDetailsWithoutCfp)

        // verify
        assertNull(conferenceDetails?.cfpSite)
    }

    @Test
    fun convertToStringWithCfpShouldWriteToStringCorrectly() {
        // act
        val conferenceDetailsStr = converter.convertToString(testApi.conferenceDetailsWithCfp)

        // verify
        assertEquals(conferenceDetailsWithCfp, conferenceDetailsStr)
    }

    @Test
    fun convertToConferenceDetailsWhenPrefixNotExistShouldReturnNull() {
        // act
        val invalidConferenceDetails = conferenceDetailsWithCfp.removePrefix("---\n")

        val conferenceDetailsStr = converter.convertToConferenceDetails(invalidConferenceDetails)

        // verify
        assertNull(conferenceDetailsStr)
    }

    @Test
    fun convertToConferenceDetailsWhenSuffixNotExistShouldReturnNull() {
        // act
        val invalidConferenceDetails = conferenceDetailsWithCfp.removeSuffix("\n---")

        val conferenceDetailsStr = converter.convertToConferenceDetails(invalidConferenceDetails)

        // verify
        assertNull(conferenceDetailsStr)
    }

    @Test
    fun convertToConferenceDetailsWithNotConferenceDetailsShouldReturnNull() {
        // prepare
        val notConferenceDetailsStr = testApi.getFileContent("conferences_response.json")

        // act
        val conferenceDetailsStr = converter.convertToConferenceDetails(notConferenceDetailsStr)

        // verify
        assertNull(conferenceDetailsStr)
    }

    @Test
    fun whenDoNotHaveEmptyLinesShouldConvertSuccessfully() {
        // prepare
        val detailsStrWithoutEmptyLines = conferenceDetailsWithoutCfp.replace("\n\n", "\n")

        // act
        val conferenceDetails = converter.convertToConferenceDetails(detailsStrWithoutEmptyLines)

        // verify
        assertNotNull(conferenceDetails)
    }

    @Test
    fun whenHaveEmptyLineInTheStartShouldConvertSuccessfully() {
        // prepare
        val detailsStrWithEmptyLineInTheStart = "\n" + conferenceDetailsWithoutCfp

        // act
        val conferenceDetails = converter.convertToConferenceDetails(detailsStrWithEmptyLineInTheStart)

        // verify
        assertNotNull(conferenceDetails)
    }

    @Test
    fun whenHaveEmptyLineInTheEndShouldConvertSuccessfully() {
        // prepare
        val detailsStrWithEmptyLineInTheStart = conferenceDetailsWithoutCfp + "\n"

        // act
        val conferenceDetails = converter.convertToConferenceDetails(detailsStrWithEmptyLineInTheStart)

        // verify
        assertNotNull(conferenceDetails)
    }

    @Test
    fun whenNameIsNotExistShouldFailToConvert() {
        // prepare
        val detailsStrWithoutName = conferenceDetailsWithoutCfp.replace("name", "replace")

        // act
        val conferenceDetails = converter.convertToConferenceDetails(detailsStrWithoutName)

        // verify
        assertNull(conferenceDetails)
    }

    @Test
    fun whenLocationIsNotExistShouldFailToConvert() {
        // prepare
        val detailsStrWithoutLocation = conferenceDetailsWithoutCfp.replace("location", "replace")

        // act
        val conferenceDetails = converter.convertToConferenceDetails(detailsStrWithoutLocation)

        // verify
        assertNull(conferenceDetails)
    }

    @Test
    fun whenDateStartIsNotExistShouldFailToConvert() {
        // prepare
        val detailsStrWithoutDateStart = conferenceDetailsWithoutCfp.replace("date_start", "replace")

        // act
        val conferenceDetails = converter.convertToConferenceDetails(detailsStrWithoutDateStart)

        // verify
        assertNull(conferenceDetails)
    }
}