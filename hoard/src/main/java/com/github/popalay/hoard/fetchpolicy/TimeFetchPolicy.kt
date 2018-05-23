package com.github.popalay.hoard.fetchpolicy

class TimeFetchPolicy<in RAW>(private val fetchingDelay: Long) : FetchPolicy<RAW> {

    companion object {

        const val SHORT_DELAY = 1_000L
        const val MEDIUM_DELAY = 5_000L
        const val LONG_DELAY = 10_000L
    }

    @Volatile private var lastFetchingTime = 0L

    override fun onFetched(data: RAW) {
        lastFetchingTime = System.currentTimeMillis()
    }

    override fun shouldFetch(data: RAW): Boolean {
        return System.currentTimeMillis() - lastFetchingTime > fetchingDelay
    }
}