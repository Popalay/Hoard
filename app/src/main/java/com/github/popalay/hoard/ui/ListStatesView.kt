package com.github.popalay.hoard.ui

import com.github.popalay.hoard.utils.Identifiable

interface ListStatesView {

    fun showRefresh()

    fun hideRefresh()

    fun showOutdatedState()

    fun showEmptyState()

    fun showErrorState()

    fun showContent(content: List<Identifiable>)
}