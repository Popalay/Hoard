package com.github.popalay.store.rest

import com.github.popalay.store.model.GithubUser
import io.reactivex.Single
import retrofit2.http.GET

interface GithubUserService {

    @GET("users")
    fun fetchUsers(): Single<List<GithubUser>>
}