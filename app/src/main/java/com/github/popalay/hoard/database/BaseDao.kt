package com.github.popalay.hoard.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

internal interface BaseDao<in T> {

    @Insert
    fun insert(data: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(data: T): Long

    @Update
    fun update(data: T)

    @Delete
    fun delete(vararg data: T)
}