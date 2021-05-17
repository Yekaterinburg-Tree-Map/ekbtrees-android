package ru.ekbtrees.treemap.ui.viewstates


sealed class MapViewState : ViewState {
    object Idle: MapViewState()
    object MapState : MapViewState()
    object MapPickTreeLocationState : MapViewState()
    class MapErrorState(val massage: String) : MapViewState()
}
