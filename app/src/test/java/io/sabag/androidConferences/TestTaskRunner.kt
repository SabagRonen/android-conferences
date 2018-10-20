package io.sabag.androidConferences

import io.sabag.androidConferences.conferencesList.TaskRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

class TestTaskRunner : TaskRunner, CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = job

    private lateinit var job: Job

    override fun run(block: suspend () -> Unit) {
        runBlocking {
            job = launch {
                block()
            }
        }
    }

    override fun cancel() {
        if (job.isActive) {
            job.cancel()
        }
    }

}