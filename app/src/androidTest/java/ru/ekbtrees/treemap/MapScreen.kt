package ru.ekbtrees.treemap

import com.kaspersky.kaspresso.screens.KScreen
import ru.ekbtrees.treemap.ui.map.TreeMapFragment

/**
 * Основной экран приложения
 */
object MapScreen : KScreen<MapScreen>() {
    override val layoutId = R.layout.fragment_tree_map
    override val viewClass = TreeMapFragment::class.java
}