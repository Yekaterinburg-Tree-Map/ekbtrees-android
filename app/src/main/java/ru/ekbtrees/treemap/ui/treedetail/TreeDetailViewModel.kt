package ru.ekbtrees.treemap.ui.treedetail

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.ui.mvi.base.BaseViewModel
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.contract.TreeDetailContract
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class TreeDetailViewModel @Inject constructor(
    private val interactor: TreesInteractor
) : BaseViewModel<TreeDetailContract.TreeDetailEvent, TreeDetailContract.TreeDetailState, TreeDetailContract.TreeMapEffect>() {
    override fun createInitialState(): TreeDetailContract.TreeDetailState {
        return TreeDetailContract.TreeDetailState.Idle
    }

    fun provideTreeId(treeId: String) {
        viewModelScope.launch {
            setState(TreeDetailContract.TreeDetailState.Loading)
            try {
                val treeDetail = interactor.getTreeDetailBy(treeId)
                setState(TreeDetailContract.TreeDetailState.Loaded(treeDetail))
            } catch (e: Exception) {
                setState(TreeDetailContract.TreeDetailState.Error)
            }
        }
    }

    override fun handleEvent(event: UiEvent) {
    }
}