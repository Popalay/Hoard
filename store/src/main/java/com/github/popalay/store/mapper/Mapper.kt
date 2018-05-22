package com.github.popalay.store.mapper

import io.reactivex.Flowable

interface Mapper<in KEY, in IN, OUT> {

    fun mapErrorWithData(key: KEY, data: IN, throwable: Throwable): Flowable<OUT>

    fun mapNext(key: KEY, data: IN): OUT

    fun mapError(throwable: Throwable): Flowable<OUT>
}