package com.github.popalay.hoard.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.github.popalay.hoard.utils.Identifiable

@Entity
data class GithubUser(
    @PrimaryKey override val id: Long,
    val login: String,
    val avatarUrl: String
) : Identifiable