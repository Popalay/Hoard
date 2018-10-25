package com.github.popalay.hoard.database

import androidx.room.*
import com.github.popalay.hoard.model.GithubUser
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

    @Transaction
    open fun deleteAndInsert(users: List<GithubUser>) {
        deleteAll()
        bulkInsert(users)
    }
}