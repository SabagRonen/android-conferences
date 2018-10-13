package io.sabag.androidConferences

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ExtraInfo (
        @StringRes val text: Int,
        @DrawableRes val icon: Int? = null
)

data class ConferenceState(
        val title: String,
        val location: String,
        val info: String,
        val extraInfo: ExtraInfo? = null
)
