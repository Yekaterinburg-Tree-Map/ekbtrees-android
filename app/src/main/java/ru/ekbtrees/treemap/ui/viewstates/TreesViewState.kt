package ru.ekbtrees.treemap.ui.viewstates

import ru.ekbtrees.treemap.domain.entity.TreeEntity

sealed class TreesViewState {
    object TreesLoadingState : TreesViewState()
    class TreesLoadedState(val trees: Collection<TreeEntity>) : TreesViewState()
    class TreesLoadingErrorState(val message: String) : TreesViewState()
}
