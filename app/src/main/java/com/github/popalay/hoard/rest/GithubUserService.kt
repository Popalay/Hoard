package com.github.popalay.hoard.rest

import com.github.popalay.hoard.model.GithubUser
import io.reactivex.Single
import retrofit2.http.GET

interface GithubUserService {

    @GET("users")
    fun fetchUsers(): Single<List<GithubUser>>
}