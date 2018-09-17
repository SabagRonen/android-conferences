package io.sabag.androidConferences

import com.google.gson.Gson
import java.util.*

class TestApi {
    val conferenceDetailsWithCfp = io.sabag.androidConferences.network.ConferenceDetails(
            name = "360|AnDev",
            website = "http://360andev.com/",
            location = "Denver, Colorado, USA",
            dateStart = getDateFromYearMonthDay(2016, Calendar.JULY, 28),
            dateEnd = getDateFromYearMonthDay(2016, Calendar.JULY, 29),
            cfpStart = getDateFromYearMonthDay(2016, Calendar.JANUARY, 27),
            cfpEnd = getDateFromYearMonthDay(2016, Calendar.APRIL, 29),
            cfpSite = "http://360andev.com/call-for-papers/"
    )

    val conferenceDetailsWithoutCfp = io.sabag.androidConferences.network.ConferenceDetails(
            name = "GDG DevFest",
            website = "https://gdgharare.com/",
            location = "Harare, Zimbabwe",
            dateStart = getDateFromYearMonthDay(2017, Calendar.OCTOBER, 14),
            dateEnd = getDateFromYearMonthDay(2017, Calendar.OCTOBER, 15),
            cfpStart = null,
            cfpEnd = null,
            cfpSite = null
    )

    private val jsonBuilder = Gson()

    fun toJson(any: Any) = jsonBuilder.toJson(any)

    fun getFileContent(filePath: String): String {
        val inputStream = javaClass.classLoader.getResourceAsStream(filePath)
        return inputStream.readBytes().toString(Charsets.UTF_8)
    }

    fun getDateFromYearMonthDay(year: Int, month: Int, day: Int) : Date {
        val calendar = Calendar.getInstance()
        calendar.set( year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}