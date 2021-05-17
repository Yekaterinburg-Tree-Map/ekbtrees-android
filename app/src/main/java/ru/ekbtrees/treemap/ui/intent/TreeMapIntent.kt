package ru.ekbtrees.treemap.ui.intent

sealed class TreeMapIntent {
    object RequestMapState : TreeMapIntent()
    object FetchTrees : TreeMapIntent()
    object NewTreeLocation : TreeMapIntent()
}