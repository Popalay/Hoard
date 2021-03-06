package com.github.popalay.hoard.fetchpolicy

object FetchPolicyFactory {

    fun <T> singleFetchPolicy() = SingleFetchPolicy<T>()

    fun <T> timeFetchPolicy(fetchingDelay: Long) = TimeFetchPolicy<T>(fetchingDelay)
}