package com.github.popalay.store.store

import com.github.popalay.store.Fetcher
import com.github.popalay.store.Persister
import com.github.popalay.store.Store
import com.github.popalay.store.database.GithubUserDao
import com.github.popalay.store.fetchpolicy.FetchPolicyFactory
import com.github.popalay.store.fetchpolicy.TimeFetchPolicy
import com.github.popalay.store.model.GithubUser
import com.github.popalay.store.rest.GithubUserService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class GithubUserStore(
    service: GithubUserService,
    dao: GithubUserDao
) : Store<List<GithubUser>, GithubUserStore.Key>(
    fetcher = GithubUserFetcher(service),
    persister = GithubUserPersister(dao),
    fetchPolicy = FetchPolicyFactory.timeFetchPolicy(TimeFetchPolicy.MEDIUM_DELAY)
) {

    sealed class Key {

        object All : Key()
    }
}

private class GithubUserFetcher(
    private val service: GithubUserService
) : Fetcher<List<GithubUser>, GithubUserStore.Key> {

    override fun fetch(key: GithubUserStore.Key): Single<List<GithubUser>> = with(key) {
        when (this) {
            GithubUserStore.Key.All -> service.fetchUsers()
        }
    }
}

private class GithubUserPersister(
    private val dao: GithubUserDao
) : Persister<List<GithubUser>, GithubUserStore.Key> {

    override fun read(key: GithubUserStore.Key): Flowable<List<GithubUser>> = with(key) {
        when (this) {
            GithubUserStore.Key.All -> dao.findAll()
        }
    }

    override fun write(data: List<GithubUser>, key: GithubUserStore.Key): Completable = Completable.fromAction {
        dao.deleteAndInsert(data)
    }

    override fun isNotEmpty(key: GithubUserStore.Key): Single<Boolean> = with(key) {
        when (this) {
            GithubUserStore.Key.All -> dao.isNotEmpty().toSingle(false)
        }
    }
}