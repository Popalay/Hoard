package com.github.popalay.store.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class GithubUser(
    @PrimaryKey val id: Long,
    val login: String,
    val avatarUrl: String
)