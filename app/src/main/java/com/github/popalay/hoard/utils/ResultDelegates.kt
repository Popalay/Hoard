package com.github.popalay.hoard.utils

import com.github.popalay.hoard.hoard.Result
import com.github.popalay.hoard.ui.ListStatesView
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object ResultDelegates {

    inline fun <T : ListStatesView> list(
        crossinline modifyContent: (List<Identifiable>) -> List<Identifiable> = { it }
    ): ReadWriteProperty<T, Result<List<Identifiable>>> =
        object : ResultDelegate<T, List<Identifiable>>() {

            override fun onLoading(thisRef: T, value: Result.Loading<List<Identifiable>>) {
                thisRef.showContent(modifyContent(value.content))
            }

            override fun onSuccess(thisRef: T, value: Result.Success<List<Identifiable>>) {
                thisRef.hideRefresh()
                thisRef.showContent(modifyContent(value.content))
            }

            override fun onOutdated(thisRef: T, value: Result.Outdated<List<Identifiable>>) {
                thisRef.hideRefresh()
                thisRef.showOutdatedState()
            }

            override fun onError(thisRef: T, value: Result.Error) {
                thisRef.hideRefresh()
                thisRef.showErrorState()
            }

            override fun onEmpty(thisRef: T, value: Result.Empty) {
                thisRef.hideRefresh()
                thisRef.showEmptyState()
            }

            override fun onIdle(thisRef: T, value: Result.Idle) {
                thisRef.showRefresh()
            }
        }
}

open class ResultDelegate<V : Any, R> : ReadWriteProperty<V, Result<R>> {

    var resource: Result<R> = Result.Idle

    override fun getValue(thisRef: V, property: KProperty<*>): Result<R> = resource

    override fun setValue(thisRef: V, property: KProperty<*>, value: Result<R>) {
        resource = value
        when (resource) {
            is Result.Loading -> onLoading(thisRef, resource as Result.Loading<R>)
            is Result.Success -> onSuccess(thisRef, resource as Result.Success<R>)
            is Result.Outdated -> onOutdated(thisRef, resource as Result.Outdated<R>)
            is Result.Error -> onError(thisRef, resource as Result.Error)
            Result.Empty -> onEmpty(thisRef, resource as Result.Empty)
            Result.Idle -> onIdle(thisRef, resource as Result.Idle)
        }
    }

    open fun onLoading(thisRef: V, value: Result.Loading<R>) {}
    open fun onSuccess(thisRef: V, value: Result.Success<R>) {}
    open fun onOutdated(thisRef: V, value: Result.Outdated<R>) {}
    open fun onError(thisRef: V, value: Result.Error) {}
    open fun onEmpty(thisRef: V, value: Result.Empty) {}
    open fun onIdle(thisRef: V, value: Result.Idle) {}
}