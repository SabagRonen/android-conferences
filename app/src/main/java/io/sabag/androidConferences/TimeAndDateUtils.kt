package io.sabag.androidConferences

import io.sabag.androidConferences.pluginInterfaces.ITimeAndDateUtils
import java.text.SimpleDateFormat
import java.util.*

class TimeAndDateUtils : ITimeAndDateUtils {
    override val currentTimeInMilliseconds: Long
        get() = System.currentTimeMillis()

    override fun getFormattedDate(date: Date): String {
        return SimpleDateFormat("yyy-MM-dd", Locale.getDefault()).format(date)
    }
}
