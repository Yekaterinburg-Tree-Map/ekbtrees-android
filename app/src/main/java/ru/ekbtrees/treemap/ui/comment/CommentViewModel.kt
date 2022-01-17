package ru.ekbtrees.treemap.ui.comment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.domain.entity.commentsEntity.NewTreeCommentEntity
import ru.ekbtrees.treemap.ui.mvi.base.BaseViewModel
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.contract.CommentContract
import java.util.*
import javax.inject.Inject
import ru.ekbtrees.treemap.domain.interactors.CommentInteractor

const val TREE_ID_KEY = "treeId"

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val interactor: CommentInteractor,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CommentContract.CommentEvent, CommentContract.CommentState, CommentContract.CommentEffect>() {

    override fun createInitialState(): CommentContract.CommentState {
        return CommentContract.CommentState.Idle
    }

    private val currTreeId: String = savedStateHandle.get<String>(TREE_ID_KEY)
        ?: error("нет параметра treeId")


    override fun handleEvent(event: UiEvent) {
        when (event) {
            is CommentContract.CommentEvent.Load -> {
                viewModelScope.launch { loadNewComment() }
            }
            is CommentContract.CommentEvent.SendCommentButtonClicked -> {
                viewModelScope.launch {
                    saveNewComment(event.text)
                    loadNewComment()
                }
            }

        }
    }

    private suspend fun loadNewComment() {
        try {
            val treeComments = interactor.getTreeCommentBy(currTreeId.toInt())
            if (treeComments.isEmpty()) {
                setState(CommentContract.CommentState.NoComments)
            } else {
                setState(CommentContract.CommentState.Loaded(treeComments.toList()))
            }
        } catch (e: Exception) {
            setState((CommentContract.CommentState.Error))
        }
    }

    private suspend fun saveNewComment(commText: String) {
        interactor.saveTreeComment(
            NewTreeCommentEntity(
                text = commText,
                treeId = currTreeId.toInt()
            )
        )
    }

}