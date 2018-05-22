package com.github.popalay.store

import android.arch.persistence.room.Room
import android.content.Context
import com.github.popalay.store.database.Database
import com.github.popalay.store.database.GithubUserDao
import com.github.popalay.store.rest.GithubUserService
import com.github.popalay.store.rest.UnsafeHttpClientBuilder
import com.github.popalay.store.store.GithubUserStore
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates

object ServiceLocator {

    const val baseUrl = "https://api.github.com/"

    var context: Context by Delegates.notNull()

    val httpClient: OkHttpClient by lazy {
        UnsafeHttpClientBuilder()
            .withLogger()
            .build()
    }

    val gson: Gson by lazy {
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .baseUrl(baseUrl)
            .build()
    }

    val githubUserService: GithubUserService by lazy {
        retrofit.create(GithubUserService::class.java)
    }

    val database: Database by lazy {
        Room
            .databaseBuilder(context, Database::class.java, "store-database")
            .build()
    }

    val githubUserDao: GithubUserDao by lazy {
        database.githubUserDao()
    }

    val githubUserStore: GithubUserStore by lazy {
        GithubUserStore(githubUserService, githubUserDao)
    }
}