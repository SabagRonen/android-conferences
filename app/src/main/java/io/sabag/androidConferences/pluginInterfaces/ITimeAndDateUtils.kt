package io.sabag.androidConferences.pluginInterfaces

import java.util.Date

interface ITimeAndDateUtils {
    val currentTimeInMilliseconds: Long
    fun getFormattedDate(date: Date): String
}
