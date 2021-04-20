package ru.ekbtrees.treemap.ui

import android.content.Context

class TreeData {
    val context : Context
    val tree : Tree

    constructor(context: Context, tree: Tree) {
        this.context = context
        this.tree = tree
    }
}