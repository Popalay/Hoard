package com.github.popalay.store.fetchpolicy

object FetchPolicyFactory {

    fun <T> singleFetchPolicy() = SingleFetchPolicy<T>()

    fun <T> timeFetchPolicy(fetchingDelay: Long) = TimeFetchPolicy<T>(fetchingDelay)
}