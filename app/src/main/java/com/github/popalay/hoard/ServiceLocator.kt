package com.github.popalay.hoard

import android.content.Context
import androidx.room.Room
import com.github.popalay.hoard.database.Database
import com.github.popalay.hoard.database.GithubUserDao
import com.github.popalay.hoard.hoard.GithubUserHoard
import com.github.popalay.hoard.rest.GithubUserService
import com.github.popalay.hoard.rest.UnsafeHttpClientBuilder
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates

object ServiceLocator {

    const val baseUrl = "https://masoud.p.mashape.com/"

    var context: Context by Delegates.notNull()

    val httpClient: OkHttpClient by lazy {
        UnsafeHttpClientBuilder()
            .unsafe()
            .withInterceptor(Interceptor { chain ->
                val original = chain.request().newBuilder()
                    .header("X-Mashape-Key", "ZFM7vNybAhmshz28Mo2b5C7DxXy7p13eh5WjsnQ2R5ptvcxTqf")
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(original)
            })
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

    val githubUserStore: GithubUserHoard by lazy {
        GithubUserHoard(githubUserService, githubUserDao)
    }
}