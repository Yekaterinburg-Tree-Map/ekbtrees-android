package ru.ekbtrees.treemap.ui.mvi.contract

import com.google.android.gms.maps.model.LatLng
import ru.ekbtrees.treemap.domain.entity.TreeDetailEntity
import ru.ekbtrees.treemap.ui.mvi.base.UiEffect
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.base.UiState
import ru.ekbtrees.treemap.ui.edittree.EditTreeFragment
import ru.ekbtrees.treemap.ui.edittree.EditTreeViewModel

/**
 * Соглашение между [EditTreeFragment] и [EditTreeViewModel]
 * */
class EditTreeContract {
    /**
     * Состояния редактирования (добавления) дерева.
     * */
    sealed class EditTreeViewState : UiState {
        object Idle : EditTreeViewState()
        object DataLoading : EditTreeViewState()
        object Error : EditTreeViewState()
        class EmptyData(val treeLocation: LatLng) : EditTreeViewState()
        class DataLoaded(val treeData: TreeDetailEntity) : EditTreeViewState()
        class NewLocationData(val treeData: TreeDetailEntity, val newLocation: LatLng) :
            EditTreeViewState()
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
        class OnSaveButtonClicked(val treeDetail: TreeDetailEntity) : EditTreeEvent()
    }

    class TreeMapEffect : UiEffect
}