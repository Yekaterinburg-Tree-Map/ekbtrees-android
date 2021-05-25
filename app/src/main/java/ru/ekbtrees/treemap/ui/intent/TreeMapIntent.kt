package ru.ekbtrees.treemap.ui.intent

sealed class TreeMapIntent {
    /**
     * Карта выведена во View элемент и готова к дальнейшему использованию.
     */
    object OnMapViewReady : TreeMapIntent()

    /**
     * Пользователь нажал на кнопку выбора позиции нового дерева.
     */
    object OnAddTreeSelected : TreeMapIntent()

    /**
     * Пользователь отменил выбор позиции нового дерева.
     */
    object OnAddTreeCanceled : TreeMapIntent()
}