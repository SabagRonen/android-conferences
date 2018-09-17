package io.sabag.androidConferences.network

import io.sabag.androidConferences.Conference
import io.sabag.androidConferences.pluginInterfaces.IConferencesNetworkClient
import io.sabag.androidConferences.ConferenceDetails as Details
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

const val BASE_URL = "https://api.github.com/repos/AndroidStudyGroup/conferences/contents/"

class ConferencesNetworkClient(baseUrl: String = BASE_URL) : IConferencesNetworkClient {

    private val service: RetrofitConferencesService by lazy {
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(ConferenceDetailsConverterFactory)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        retrofit.create(RetrofitConferencesService::class.java)
    }

    override fun getConferencesList(): List<Conference> {
        val conferencesList = mutableListOf<Conference>()
        try {
            val call = service.getConferencesContent()
            val response = call.execute()
            response.body()?.forEach { content ->
                content.downloadUrl?.let { downloadUrl ->
                    conferencesList.add(Conference(downloadUrl))
                }
            }
        } finally {
            return conferencesList
        }
    }

    override fun getConferenceDetails(conferenceId: String): Details? {
        val call = service.getConferenceDetails(conferenceId)
        var response: Response<ConferenceDetails>? = null
        try {
            response = call.execute()
        } finally {
            return response?.body()?.toDetails(conferenceId)
        }
    }

    private fun ConferenceDetails.toDetails(id: String) = Details (
            id = id,
            name = name ?: "",
            location = location ?: "",
            startDate = dateStart ?: Date(0),
            endDate = dateEnd
    )
}
