package com.github.popalay.hoard.hoard

import com.github.popalay.hoard.Fetcher
import com.github.popalay.hoard.Hoard
import com.github.popalay.hoard.Persister
import com.github.popalay.hoard.database.GithubUserDao
import com.github.popalay.hoard.fetchpolicy.FetchPolicyFactory
import com.github.popalay.hoard.fetchpolicy.TimeFetchPolicy
import com.github.popalay.hoard.model.GithubUser
import com.github.popalay.hoard.rest.GithubUserService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class GithubUserHoard(
    service: GithubUserService,
    dao: GithubUserDao
) : Hoard<List<GithubUser>, GithubUserHoard.Key>(
    fetcher = GithubUserFetcher(service),
    persister = GithubUserPersister(dao),
    fetchPolicy = FetchPolicyFactory.timeFetchPolicy(TimeFetchPolicy.MEDIUM_DELAY)
) {

    sealed class Key : com.github.popalay.hoard.Key {

        object All : Key()
    }
}

private class GithubUserFetcher(
    private val service: GithubUserService
) : Fetcher<List<GithubUser>, GithubUserHoard.Key> {

    override fun fetch(key: GithubUserHoard.Key): Single<List<GithubUser>> = with(key) {
        when (this) {
            GithubUserHoard.Key.All -> service.fetchUsers()
        }
    }
}

private class GithubUserPersister(
    private val dao: GithubUserDao
) : Persister<List<GithubUser>, GithubUserHoard.Key> {

    override fun read(key: GithubUserHoard.Key): Flowable<List<GithubUser>> = with(key) {
        when (this) {
            GithubUserHoard.Key.All -> dao.findAll()
        }
    }

    override fun write(data: List<GithubUser>, key: GithubUserHoard.Key): Completable = Completable.fromAction {
        dao.deleteAndInsert(data)
    }

    override fun isNotEmpty(key: GithubUserHoard.Key): Single<Boolean> = with(key) {
        when (this) {
            GithubUserHoard.Key.All -> dao.isNotEmpty().toSingle(false)
        }
    }
}