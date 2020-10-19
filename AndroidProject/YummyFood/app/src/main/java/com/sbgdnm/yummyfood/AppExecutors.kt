package com.sbgdnm.yummyfood

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Global executor pools for the whole application.
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */

class AppExecutors {

    private val networkIO: ScheduledExecutorService = Executors.newScheduledThreadPool(3)

    fun networkIO(): ScheduledExecutorService {
        return networkIO
    }

    companion object {
        private var INSTANCE: AppExecutors? = null
        fun getInstance() = INSTANCE
            ?: AppExecutors().also {
                INSTANCE = it
            }
    }
}