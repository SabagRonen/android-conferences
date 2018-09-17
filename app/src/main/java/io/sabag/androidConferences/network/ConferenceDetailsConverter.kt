package io.sabag.androidConferences.network

import java.text.SimpleDateFormat
import java.util.*

class ConferenceDetailsConverter {

    fun convertToConferenceDetails(detailsStr: String): ConferenceDetails? {
        val lines = detailsStr.lines().filter { it.isNotEmpty() }

        if (lines.first() != "---" || lines.last() != "---") {
            return null
        }
        var name: String? = null
        var website: String? = null
        var location: String? = null
        var startDate: Date? = null
        var endDate: Date? = null
        var cfpStart: Date? = null
        var cfpEnd: Date? = null
        var cfpSite: String? = null
        lines.forEach { line ->
            if (line.contains("name")) {
                name = getValue(line, "name", 1, 1)
            }
            if (line.contains("website")) {
                website = getValue(line, "website")
            }
            if (line.contains("location")) {
                location = getValue(line, "location")
            }
            if (line.contains("date_start")) {
                startDate = getDateValue(line, "date_start")
            }
            if (line.contains("date_end")) {
                endDate = getDateValue(line, "date_end")
            }
            if (line.contains("cfp_start")) {
                cfpStart = getDateValue(line, "cfp_start")
            }
            if (line.contains("cfp_end")) {
                cfpEnd = getDateValue(line, "cfp_end")
            }
            if (line.contains("cfp_site")) {
                cfpSite = getValue(line, "cfp_site")
            }

        }
        if (name == null || location == null || startDate == null) {
            return null
        }
        return ConferenceDetails(name = name, website = website, location = location,
                dateStart = startDate, dateEnd = endDate, cfpStart = cfpStart, cfpEnd = cfpEnd,
                cfpSite = cfpSite)
    }

    fun convertToString(conferenceDetails: ConferenceDetails) : String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("---\n")
        stringBuilder.append("name: \"${conferenceDetails.name?.let{it} ?: ""}\"\n")
        stringBuilder.append("website: ${conferenceDetails.website?.let{it} ?: ""}\n")
        stringBuilder.append("location: ${conferenceDetails.location?.let{it} ?: ""}\n\n")
        stringBuilder.append("date_start: ${conferenceDetails.dateStart?.let {formatter.format(it)} ?: ""}\n")
        stringBuilder.append("date_end:   ${conferenceDetails.dateEnd?.let {formatter.format(it)} ?: ""}\n")
        if (conferenceDetails.cfpStart != null) {
            stringBuilder.append("\ncfp_start: ${formatter.format(conferenceDetails.cfpStart)}\n")
        }
        if (conferenceDetails.cfpEnd != null) {
            stringBuilder.append("cfp_end:   ${formatter.format(conferenceDetails.cfpEnd)}\n")
        }
        if (conferenceDetails.cfpSite != null) {
            stringBuilder.append("cfp_site: ${conferenceDetails.cfpSite}\n")
        }
        stringBuilder.append("---")
        return stringBuilder.toString()
    }

    private fun getValue(
            line: String,
            key: String,
            startOffset: Int = 0,
            endOffset: Int = 0
    ) = line.substring(key.length + ": ".length + startOffset, line.length - endOffset)

    private fun getDateValue(line: String, key: String) : Date? {
        val dateStr = getValue(line, key)
        return formatter.parse(dateStr)
    }

    private val formatter: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

}
