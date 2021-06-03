package ru.ekbtrees.treemap.ui.mvi.contract

import ru.ekbtrees.treemap.domain.entity.TreeDetailEntity
import ru.ekbtrees.treemap.ui.mvi.base.UiEffect
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.base.UiState

class EditTreeContract {
    /**
     * Состояния редактирования (добавления) дерева.
     * */
    sealed class EditTreeViewState : UiState {
        object Idle : EditTreeViewState()
        object NewTreeState : EditTreeViewState()
        object TreeDataLoadingState : EditTreeViewState()
        class TreeDataLoadedState(val treeData: TreeDetailEntity) : EditTreeViewState()
        class MapErrorState(val massage: String) : EditTreeViewState()
    }

    /**
     * Интенты, которые будут поступать к ViewModel.
     * */
    sealed class EditTreeEvent : UiEvent {
        /**
         * Инициируем повторную загрузку данных.
         * */
        object OnReloadDataLaunched : EditTreeEvent()

        /**
         * Инициируем сохранение введённых данных.
         */
        class OnSaveDataLaunched(val treeData: TreeDetailEntity) : EditTreeEvent()
    }

    class TreeMapEffect : UiEffect
}