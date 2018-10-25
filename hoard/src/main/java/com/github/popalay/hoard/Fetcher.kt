package com.github.popalay.hoard

import io.reactivex.Flowable

/**
 * Created by Denys Nykyforov on 18.12.2017
 * Copyright (c) 2017. All right reserved
 */
interface Fetcher<RAW, in KEY> {

    fun fetch(key: KEY): Flowable<RAW>
}