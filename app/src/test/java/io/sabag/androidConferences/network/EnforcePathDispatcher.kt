package io.sabag.androidConferences.network

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import java.net.HttpURLConnection

object EnforcePathDispatcher : QueueDispatcher() {

    private var queueIndex = 0
    private var expectedPathsList = mutableListOf<String>()


    override fun dispatch(request: RecordedRequest?): MockResponse {
        val response = responseQueue.take()
        val expectedPath = expectedPathsList.elementAtOrNull(queueIndex++)
        if (request?.path != expectedPath) {
            return MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
        }
        return response
    }

    fun clearResponseQueue() {
        responseQueue.clear()
        expectedPathsList.clear()
        queueIndex = 0
    }

    fun addExpectedPath(path: String) {
        expectedPathsList.add(path)
    }
}