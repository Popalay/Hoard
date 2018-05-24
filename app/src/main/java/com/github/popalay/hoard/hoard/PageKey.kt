package com.github.popalay.hoard.hoard

import com.github.popalay.hoard.Key
import java.util.Date

internal const val DEFAULT_PAGE_SIZE = 20

interface PageKey : Key {
    val pageSize get() = DEFAULT_PAGE_SIZE
    val date: Date get() = Date()
    val offset: Int
}