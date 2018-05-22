package com.github.popalay.store.database

import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Update

internal interface BaseDao<in T> {

    @Insert fun insert(data: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(data: T): Long

    @Update fun update(data: T)

    @Delete fun delete(vararg data: T)
}