package ru.ekbtrees.treemap.ui.treedetail

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.ekbtrees.treemap.domain.entity.TreeDetailEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.ui.edittree.EditTreeInstanceValue
import ru.ekbtrees.treemap.ui.mvi.base.BaseViewModel
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.contract.EditTreeContract
import ru.ekbtrees.treemap.ui.mvi.contract.TreeDetailContract
import javax.inject.Inject

@HiltViewModel
class TreeDetailViewModel @Inject constructor(
    private val interactor: TreesInteractor
) : BaseViewModel<TreeDetailContract.TreeDetailEvent, TreeDetailContract.TreeDetailState, TreeDetailContract.TreeMapEffect>() {
    override fun createInitialState(): TreeDetailContract.TreeDetailState {
        return TreeDetailContract.TreeDetailState.Idle
    }

    fun provideInstanceValue(instanceValue: TreeDetailEntity) {
        setState(TreeDetailContract.TreeDetailState.Loaded(instanceValue))
    }

    override fun handleEvent(event: UiEvent) {
    }
}