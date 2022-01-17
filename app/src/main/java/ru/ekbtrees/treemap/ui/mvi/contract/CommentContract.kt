package ru.ekbtrees.treemap.ui.mvi.contract

import ru.ekbtrees.treemap.domain.entity.commentsEntity.*
import ru.ekbtrees.treemap.domain.repositories.UploadResult
import ru.ekbtrees.treemap.ui.comment.CommentView
import ru.ekbtrees.treemap.ui.mvi.base.UiEffect
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.base.UiState

class CommentContract {
    sealed class CommentState : UiState {
        object Idle : CommentState()
        object Loading : CommentState()
        object Error : CommentState()
        object NoComments : CommentState()
        data class Loaded(var comments: List<TreeCommentEntity>) : CommentState()
    }
    sealed class CommentEvent: UiEvent {
        data class SendCommentButtonClicked(val text: String) : CommentEvent()
        object Load: CommentEvent()
    }
    sealed class CommentEffect : UiEffect
}