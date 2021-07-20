package ru.ekbtrees.treemap.ui.mvi.contract

import ru.ekbtrees.treemap.domain.entity.ClusterTreesEntity
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.ui.map.TreeMapFragment
import ru.ekbtrees.treemap.ui.map.TreeMapViewModel
import ru.ekbtrees.treemap.ui.mvi.base.UiEffect
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.base.UiState

/**
 * Соглашение между [TreeMapFragment] и [TreeMapViewModel].
 * */
class TreeMapContract {

    /**
     * Состояния карты деревьев
     * */
    sealed class MapViewState : UiState {
        object Idle : MapViewState()
        object MapState : MapViewState()
        object MapPickTreeLocationState : MapViewState()
        class MapErrorState(val massage: String) : MapViewState()
    }

    /**
     * Интенты, которые будут поступать к ViewModel.
     * */
    sealed class TreeMapEvent : UiEvent {
        /**
         * Карта выведена во View элемент и готова к дальнейшему использованию.
         */
        object OnMapViewReady : TreeMapEvent()

        /**
         * Пользователь нажал на кнопку выбора позиции нового дерева.
         */
        object OnAddTreeButtonClicked : TreeMapEvent()

        /**
         * Пользователь отменил выбор позиции нового дерева.
         */
        object OnAddTreeCanceled : TreeMapEvent()
    }

    /**
     * Состояния данных.
     * */
    sealed class DataState {
        object Idle : DataState()
        object Loading : DataState()
        object Error: DataState()
        class Loaded(val data: LoadedData) : DataState()
    }

    /**
     * Объект данных для [DataState.Loaded]
     * */
    sealed class LoadedData {
        data class Trees(val trees: Collection<TreeEntity>) : LoadedData()
        data class TreeClusters(val clusterTrees: Collection<TreeEntity>) : LoadedData()
    }

    class TreeMapEffect : UiEffect
}