package com.github.popalay.store.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.github.popalay.store.model.GithubUser
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
abstract class GithubUserDao : BaseDao<GithubUser> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun bulkInsert(articles: List<GithubUser>)

    @Query("SELECT * FROM githubuser")
    abstract fun findAll(): Flowable<List<GithubUser>>

    @Query("DELETE FROM githubuser")
    abstract fun deleteAll()

    @Query("""SELECT count(*) FROM githubuser LIMIT 1""")
    abstract fun isNotEmpty(): Maybe<Boolean>

    @Transaction open fun deleteAndInsert(users: List<GithubUser>) {
        deleteAll()
        bulkInsert(users)
    }
}