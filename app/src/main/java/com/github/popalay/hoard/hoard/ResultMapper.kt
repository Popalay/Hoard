package com.github.popalay.hoard.hoard

import android.arch.persistence.room.EmptyResultSetException
import com.github.popalay.hoard.Hoard
import com.github.popalay.hoard.Key
import com.github.popalay.hoard.fetchpolicy.FetchPolicy
import com.github.popalay.hoard.mapper.Mapper
import com.github.popalay.hoard.utils.isConnectivityExceptions
import io.reactivex.Flowable

internal class ResultMapper<in KEY, IN>(
    private val fetchPolicy: FetchPolicy<IN>,
    private val dataIsEmpty: (data: IN) -> Boolean
) : Mapper<KEY, IN, Result<IN>> {

    override fun mapNext(key: KEY, data: IN) = when {
        fetchPolicy.shouldFetch(data) -> Result.Loading(data)
        !fetchPolicy.shouldFetch(data) && !dataIsEmpty(key, data) -> Result.Success(data)
        else -> Result.Empty
    }

    override fun mapError(throwable: Throwable): Flowable<Result<IN>> = when {
        throwable is EmptyResultSetException || throwable is NoSuchElementException -> Flowable.just(Result.Empty)
        throwable.isConnectivityExceptions() -> Flowable.just(Result.Error(throwable))
        else -> Flowable.error(throwable)
    }

    override fun mapErrorWithData(key: KEY, data: IN, throwable: Throwable): Flowable<Result<IN>> =
        if (dataIsEmpty(key, data)) {
            Flowable.just(Result.Error(throwable))
        } else {
            Flowable.just(Result.Outdated(throwable))
        }

    private fun dataIsEmpty(key: KEY, data: IN): Boolean {
        val dataIsEmpty = dataIsEmpty(data)
        return dataIsEmpty && (key as? PageKey)?.offset ?: 0 == 0
    }
}

internal fun <KEY : Key, RAW> Hoard<RAW, KEY>.getWithResult(
    key: KEY,
    dataIsEmpty: (data: RAW) -> Boolean = { false }
): Flowable<Result<RAW>> = flow(
    key,
    ignoreConnectivity = false,
    mapper = ResultMapper(fetchPolicy, dataIsEmpty)
).startWith(Result.Idle)