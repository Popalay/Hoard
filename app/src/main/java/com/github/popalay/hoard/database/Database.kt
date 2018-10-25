package com.github.popalay.hoard.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.popalay.hoard.model.GithubUser

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