package com.github.popalay.store.fetchpolicy

/**
 * Created by Denys Nykyforov on 18.12.2017
 * Copyright (c) 2017. All right reserved
 */
interface FetchPolicy<in RAW> {

    fun onFetched(data: RAW)

    fun shouldFetch(data: RAW): Boolean
}