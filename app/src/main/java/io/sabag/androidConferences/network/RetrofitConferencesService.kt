package io.sabag.androidConferences.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitConferencesService {
    @GET("_conferences?ref=gh-pages")
    fun getConferencesContent(): Call<List<Content>>

    @GET
    fun getConferenceDetails(@Url url: String): Call<ConferenceDetails>
}