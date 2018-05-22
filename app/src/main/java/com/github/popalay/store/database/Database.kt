package com.github.popalay.store.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.github.popalay.store.model.GithubUser

@Database(
    entities = [
        GithubUser::class
    ],
    version = 1
)
@TypeConverters
abstract class Database : RoomDatabase() {

    abstract fun githubUserDao(): GithubUserDao
}