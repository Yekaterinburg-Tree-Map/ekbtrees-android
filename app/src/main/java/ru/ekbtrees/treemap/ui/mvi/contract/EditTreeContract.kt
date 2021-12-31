package ru.ekbtrees.treemap.ui.mvi.contract

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
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

        /**
         * Инициируем сохранение введённых данных.
         */
        class OnSaveButtonClicked(val treeDetail: TreeDetailFragmentModel) : EditTreeEvent()

        class OnImagesSelected(
            //val treeDetail: TreeDetailFragmentModel,
            val image: List<Bitmap>
        ) : EditTreeEvent()
    }

    sealed class TreeDetailEffect : UiEffect {
        object BackOnBackStack : TreeDetailEffect()
        object ShowErrorMessage : TreeDetailEffect()
    }

    sealed class TreeDetailFragmentModel : Parcelable {
        @Parcelize
        data class TreeDetail(val treeDetail: TreeDetailUIModel) : TreeDetailFragmentModel()

        @Parcelize
        data class NewTreeDetail(val newTreeDetail: NewTreeDetailUIModel) :
            TreeDetailFragmentModel()
    }
}