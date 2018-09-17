package io.sabag.androidConferences

import org.mockito.ArgumentCaptor
import org.mockito.Mockito

fun <T> anyNonNull(): T = Mockito.any<T>() as T
inline fun <reified T> lambdaArgumentCaptor() : ArgumentCaptor<T> = ArgumentCaptor.forClass(T::class.java)
fun <T> cap(argumentCaptor: ArgumentCaptor<T>) : T = argumentCaptor.capture()
inline fun <reified T> lambdaMock() : T = Mockito.mock(T::class.java)