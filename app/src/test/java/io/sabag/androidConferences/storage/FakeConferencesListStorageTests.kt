package io.sabag.androidConferences.storage

class FakeConferencesListStorageTests : IConferencesListStorageTests() {
    override fun getActualConferencesStorageClient() = FakeConferencesStorageClient()
}