[![Download](https://api.bintray.com/packages/popalay/maven/Tutors/images/download.svg) ](https://bintray.com/popalay/maven/Hoard/_latestVersion)
[![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![License](https://img.shields.io/badge/license-Apache--2.0-green.svg)](https://github.com/Popalay/Hoard/blob/master/LICENSE)

# Hoard
Kotlin library for reactive data loading

## Main advantages
+ separated persister and fetcher
+ contains fetch policy
+ supports RxJava2 
+ persister returns Flowable
+ simple to extend
+ suitable for Unit Testing
+ 100% Kotlin code

## How to add
Add the dependency in your build.gradle:
```groovy
dependencies {
    implementation "com.popalay.hoard:hoard:$hoardVersion"
}
```
## Usage

Create Hoard instance and observe changes
```kotlin
GithubUserHoard(githubUserService, githubUserDao)
  .get(GithubUserHoard.Key.All, dataIsEmpty = { it.isEmpty() })
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(adapter::submitList, ::handleError)
```

## [Sample](app/src/main/java/com/github/popalay/hoard/)

Hoard implementation
```kotlin
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
```
Persister implementation
```kotlin
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
```
Fetcher implementation
```kotlin
private class GithubUserFetcher(
    private val service: GithubUserService
) : Fetcher<List<GithubUser>, GithubUserHoard.Key> {

    override fun fetch(key: GithubUserHoard.Key): Single<List<GithubUser>> = with(key) {
        when (this) {
            GithubUserHoard.Key.All -> service.fetchUsers()
        }
    }
}
```
Custom mapper
```kotlin 
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
```

## Developed by

[Denys Nykyforov](https://github.com/Popalay)  
[Ruslan Sierov](https://github.com/Augusent)  

## License

```
Copyright (c) 2018 Denys Nykyforov (@popalay)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
