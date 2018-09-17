package io.sabag.androidConferences

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.sabag.androidConferences.conferencesList.ConferencesListModule

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [ConferencesListModule::class])
    abstract fun bindMainActivity() : MainActivity
}