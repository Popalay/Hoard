package com.github.popalay.hoard

import com.github.popalay.hoard.fetchpolicy.FetchPolicy
import com.github.popalay.hoard.fetchpolicy.FetchPolicyFactory
import com.github.popalay.hoard.mapper.DefaultMapper
import com.github.popalay.hoard.mapper.Mapper
import com.github.popalay.hoard.utils.isConnectivityExceptions
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Denys Nykyforov on 16.12.2017
 * Copyright (c) 2017. All right reserved
 */
//TODO add builder
open class Hoard<RAW, in KEY>(
    private val fetcher: Fetcher<RAW, KEY>,
    private val persister: Persister<RAW, KEY>,
    private val fetchPolicy: FetchPolicy<RAW> = FetchPolicyFactory.singleFetchPolicy()
) {

    fun get(key: KEY, ignoreConnectivity: Boolean = false): Flowable<RAW> = flow(
        key,
        ignoreConnectivity,
        DefaultMapper()
    )

    private fun <OUT> flow(
        key: KEY,
        ignoreConnectivity: Boolean = false,
        mapper: Mapper<KEY, RAW, OUT>
    ): Flowable<OUT> = persister.isNotEmpty(key)
        .subscribeOn(Schedulers.io())
        .flatMapPublisher { isNotEmpty ->
            if (isNotEmpty) {
                persister.read(key)
                    .subscribeOn(Schedulers.io())
                    .flatMap { raw ->
                        if (fetchPolicy.shouldFetch(raw)) {
                            fetchAndStore(key, ignoreConnectivity)
                                .toFlowable<OUT>()
                                .onErrorResumeNext { throwable: Throwable ->
                                    mapper.mapErrorWithData(key, raw, throwable)
                                }
                                .startWith(mapper.mapNext(key, raw))
                        } else {
                            Flowable.just(mapper.mapNext(key, raw))
                        }
                    }
            } else {
                fetchAndStore(key, ignoreConnectivity)
                    .andThen(persister.read(key))
                    .map { mapper.mapNext(key, it) }
            }
        }
        .onErrorResumeNext(mapper::mapError)
        .distinctUntilChanged()

    private fun fetchAndStore(key: KEY, ignoreConnectivity: Boolean): Completable = fetcher.fetch(key)
        .subscribeOn(Schedulers.io())
        .doOnSuccess(fetchPolicy::onFetched)
        .flatMapCompletable { persister.write(it, key).subscribeOn(Schedulers.io()) }
        .onErrorComplete { ignoreConnectivity && it.isConnectivityExceptions() }
}