package ru.ekbtrees.treemap.ui.mvi.contract

import ru.ekbtrees.treemap.ui.mvi.base.UiEffect
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.base.UiState
import ru.ekbtrees.treemap.ui.edittree.EditTreeFragment
import ru.ekbtrees.treemap.ui.edittree.EditTreeViewModel
import ru.ekbtrees.treemap.ui.model.NewTreeDetailUIModel
import ru.ekbtrees.treemap.ui.model.TreeDetailUIModel

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
        class NewTreeData(val treeDetail: NewTreeDetailUIModel) : EditTreeViewState()
        class DataLoaded(val treeData: TreeDetailUIModel) : EditTreeViewState()
    }

    /**
     * Интенты, которые будут поступать к ViewModel.
     * */
    sealed class EditTreeEvent : UiEvent {
        /**
         * Инициируем загрузку данных дерева.
         * */
        object OnReloadButtonClicked : EditTreeEvent()

        class OnEditField(val newValue: EditTreeInputField) : EditTreeEvent()

        /**
         * Инициируем сохранение введённых данных.
         */
        object OnSaveButtonClicked : EditTreeEvent()

        class OnImagesSelected(val filePath: List<String>) : EditTreeEvent()
    }

    sealed class TreeDetailEffect : UiEffect {
        object BackOnBackStack : TreeDetailEffect()
        object ShowErrorMessage : TreeDetailEffect()
    }
}

sealed class EditTreeInputField {
    class HeightOfTheFirstBranch(val newValue: String) : EditTreeInputField()
    class NumberOfTrunks(val newValue: String) : EditTreeInputField()
    class TrunkGirth(val newValue: String) : EditTreeInputField()
    class DiameterOfCrown(val newValue: String) : EditTreeInputField()
    class ConditionAssessment(val newValue: String) : EditTreeInputField()
    class Age(val newValue: String) : EditTreeInputField()
    class PlantingType(val newValue: String) : EditTreeInputField()
    class Species(val newValue: String) : EditTreeInputField()
    class Status(val newValue: String) : EditTreeInputField()
}