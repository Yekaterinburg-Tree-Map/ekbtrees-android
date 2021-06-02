package ru.ekbtrees.treemap.ui.treedetail

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.ui.mvi.base.BaseViewModel
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.contract.TreeDetailContract
import javax.inject.Inject

@HiltViewModel
class TreeDetailViewModel @Inject constructor(
    private val interactor: TreesInteractor
) : BaseViewModel<TreeDetailContract.TreeDetailEvent, TreeDetailContract.TreeDetailViewState, TreeDetailContract.TreeMapEffect>() {
    override fun createInitialState(): TreeDetailContract.TreeDetailViewState {
        return TreeDetailContract.TreeDetailViewState.Idle
    }

    override fun handleEvent(event: UiEvent) {
    }
}