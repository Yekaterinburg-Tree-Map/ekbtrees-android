package ru.ekbtrees.treemap.ui.mvi.contract

import com.google.android.gms.maps.model.LatLng
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
        object TreeDataLoadingState : EditTreeViewState()
        object TreeDataLoadingFailedState : EditTreeViewState()
        class EmptyTreeDataState(val treeLocation: LatLng) : EditTreeViewState()
        class TreeDataLoadedState(val treeData: TreeDetailEntity) : EditTreeViewState()
    }

    /**
     * Интенты, которые будут поступать к ViewModel.
     * */
    sealed class EditTreeEvent : UiEvent {
        /**
         * Инициируем загрузку данных дерева.
         * */
        class OnReloadButtonClicked(val treeId: String) : EditTreeEvent()

        /**
         * Инициируем сохранение введённых данных.
         */
        class OnSaveButtonClicked(val treeData: TreeDetailEntity) : EditTreeEvent()
    }

    class TreeMapEffect : UiEffect
}