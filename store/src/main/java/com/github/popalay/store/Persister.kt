package com.github.popalay.store

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface Persister<RAW, in KEY> {

    fun write(data: RAW, key: KEY): Completable

    fun read(key: KEY): Flowable<RAW>

    fun isNotEmpty(key: KEY): Single<Boolean>
}