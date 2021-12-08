package ru.ekbtrees.treemap.ui.mvi.contract

import ru.ekbtrees.treemap.ui.mvi.base.UiEffect
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.base.UiState

class CommentFragmentContract {
    sealed class CommentFragmentState : UiState {
        object Idle : CommentFragmentState()
    }
    sealed class CommentFragmentEvent: UiEvent
    class CommentFragmentEffect : UiEffect
}