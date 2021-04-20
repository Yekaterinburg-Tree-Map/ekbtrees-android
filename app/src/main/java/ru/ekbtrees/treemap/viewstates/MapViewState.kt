package ru.ekbtrees.treemap.viewstates

import ru.ekbtrees.treemap.domain.entity.TreeEntity

sealed class MapViewState {
    object MapAndTreesLoadingState: MapViewState()
    object MapAddNewTreeState : MapViewState()
    class TreeLoadedState(val trees: Collection<TreeEntity>): MapViewState()
    class MapErrorState(val massage: String): MapViewState()
}
