package io.sabag.androidConferences.conferencesList

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface TaskRunner {
    fun run(block: suspend () -> Unit)
    fun cancel()
}

class LaunchTaskRunner : TaskRunner, CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private lateinit var job: Job
    override fun run(block: suspend () -> Unit) {
        job = launch {
            block()
        }
    }

    override fun cancel() {
        if (job.isActive) {
            job.cancel()
        }
    }
}

class BlockingTaskRunner : TaskRunner {
    override fun run(block:  suspend() -> Unit) {
        runBlocking {
            block()
        }
    }

    override fun cancel() {

    }
}