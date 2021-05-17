package ru.ekbtrees.treemap.ui.viewstates

import ru.ekbtrees.treemap.domain.entity.TreeEntity

sealed class TreesViewState : ViewState {
    object Idle : TreesViewState()
    object TreesLoadingState : TreesViewState()
    class TreesLoadedState(val trees: Array<TreeEntity>) : TreesViewState()
    class TreesLoadingErrorState(val message: String) : TreesViewState()
}
