package ru.ekbtrees.treemap.ui.viewstates


sealed class MapViewState {
    object MapLoadingState : MapViewState()
    object MapAddNewTreeState : MapViewState()
    object MapLoadedState : MapViewState()
    class MapErrorState(val massage: String) : MapViewState()
}
