package ru.ekbtrees.treemap.ui.mvi.contract

import ru.ekbtrees.treemap.domain.entity.TreeDetailEntity
import ru.ekbtrees.treemap.ui.mvi.base.UiEffect
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.base.UiState
import ru.ekbtrees.treemap.ui.treedetail.TreeDetailFragment
import ru.ekbtrees.treemap.ui.treedetail.TreeDetailViewModel
import ru.ekbtrees.treemap.ui.SharedViewModel

/**
 * Контракт между [TreeDetailFragment] и [TreeDetailViewModel]
 * */
class TreeDetailContract {

    /**
     * Состояния экрана детализации дерева.
     * */
    sealed class TreeDetailState : UiState {
        object Idle : TreeDetailState()
        object Loading : TreeDetailState()
        object Error : TreeDetailState()
        class Loaded(val treeDetailEntity: TreeDetailEntity) :
            TreeDetailState()
    }

    /**
     * Интенты, которые будут поступать к ViewModel.
     * Пустой, потому что фрагмент корректнее обработает переход к редактированию дерева через [SharedViewModel].
     * */
    sealed class TreeDetailEvent : UiEvent

    class TreeMapEffect : UiEffect
}