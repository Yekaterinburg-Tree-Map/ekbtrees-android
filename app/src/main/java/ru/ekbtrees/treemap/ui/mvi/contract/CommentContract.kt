package ru.ekbtrees.treemap.ui.mvi.contract

import org.w3c.dom.Comment
import ru.ekbtrees.treemap.domain.entity.TreeDetailEntity
import ru.ekbtrees.treemap.ui.comment.CommentView
import ru.ekbtrees.treemap.ui.mvi.base.UiEffect
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.base.UiState

class CommentContract {
    sealed class CommentState : UiState {
        object Idle : CommentState()
        object Loading : CommentState()
        object Error : CommentState()
        data class Loaded(val comments : List<CommentView>) : CommentState()
    }
    sealed class CommentEvent: UiEvent {
        object SendCommentButtonClicked : CommentEvent()
    }
    class CommentEffect : UiEffect
}