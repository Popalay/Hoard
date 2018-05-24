package com.github.popalay.hoard.hoard

sealed class Result<out T> {

    data class Loading<out T>(val content: T) : Result<T>()
    data class Success<out T>(val content: T) : Result<T>()
    object Empty : Result<Nothing>()
    object Idle : Result<Nothing>()
    data class Outdated<out T>(val throwable: Throwable) : Result<T>()
    data class Error(val throwable: Throwable) : Result<Nothing>()

    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Loading -> Loading(transform(content))
        is Success -> Success(transform(content))
        is Outdated -> Outdated(throwable)
        is Error -> this
        Empty -> Empty
        Idle -> Idle
    }
}