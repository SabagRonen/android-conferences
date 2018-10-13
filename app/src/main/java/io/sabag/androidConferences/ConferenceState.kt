package io.sabag.androidConferences

import androidx.annotation.DrawableRes

data class ExtraInfo (
        val text: String,
        @DrawableRes val icon: Int? = null
)

data class ConferenceState(
        val title: String,
        val location: String,
        val info: String,
        val extraInfo: ExtraInfo? = null
)
