package com.github.popalay.store.mapper

import io.reactivex.Flowable

/**
 * Created by Denys Nykyforov on 18.12.2017
 * Copyright (c) 2017. All right reserved
 */
internal class DefaultMapper<in KEY, IN> : Mapper<KEY, IN, IN> {

    override fun mapNext(key: KEY, data: IN) = data

    override fun mapError(throwable: Throwable): Flowable<IN> = Flowable.error(throwable)

    override fun mapErrorWithData(key: KEY, data: IN, throwable: Throwable): Flowable<IN> = Flowable.error(throwable)
}