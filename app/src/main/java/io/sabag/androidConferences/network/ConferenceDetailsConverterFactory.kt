package io.sabag.androidConferences.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

object ConferenceDetailsConverterFactory : Converter.Factory() {

    private val converter: ConferenceDetailsConverter by lazy {
        ConferenceDetailsConverter()
    }

    override fun responseBodyConverter(
            type: Type?,
            annotations: Array<out Annotation>?,
            retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {

        if (type == ConferenceDetails::class.java) {
            return Converter<ResponseBody, ConferenceDetails> { value ->
                val body = value.string()
                if (body == null) {
                    throw Throwable("Expected body of ConferenceDetails but was $body")
                }
                converter.convertToConferenceDetails(body)
            }
        }
        return null
    }

    override fun requestBodyConverter(
            type: Type?,
            parameterAnnotations: Array<out Annotation>?,
            methodAnnotations: Array<out Annotation>?,
            retrofit: Retrofit?
    ): Converter<*, RequestBody>? {
        return null
    }

    override fun stringConverter(
            type: Type?,
            annotations: Array<out Annotation>?,
            retrofit: Retrofit?
    ): Converter<*, String>? {
        return null
    }
}