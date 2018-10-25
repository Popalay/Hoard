package com.github.popalay.hoard.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.popalay.hoard.utils.Identifiable

@Entity
data class GithubUser(
    @PrimaryKey override val id: Long,
    val login: String,
    val avatarUrl: String
) : Identifiable