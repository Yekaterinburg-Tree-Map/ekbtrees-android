package ru.ekbtrees.treemap.ui.comment

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
import ru.ekbtrees.treemap.domain.repositories.UploadResult
import ru.ekbtrees.treemap.ui.mappers.toNewCommentEntity
import ru.ekbtrees.treemap.ui.model.NewTreeCommentUIModel


@HiltViewModel
class CommentViewModel @Inject constructor(
    private val interactor: CommentInteractor
) : BaseViewModel<CommentContract.CommentEvent, CommentContract.CommentState, CommentContract.CommentEffect>() {

    override fun createInitialState(): CommentContract.CommentState {
        return CommentContract.CommentState.Idle
    }

    private lateinit var currTreeId: String

    fun provideTreeId(treeId: String) {
        currTreeId = treeId
    }

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
            val treeComments = interactor.getTreeCommentBy(currTreeId)
            setState(CommentContract.CommentState.Loaded(treeComments.toList()))
        } catch (e: Exception) {
            setState((CommentContract.CommentState.Error))
        }
    }

    private suspend fun saveNewComment(commText: String){
        val arr = arrayOf(Constants.UsersNames.ME.name, Constants.UsersNames.ANOTHER_USER.name)
        interactor.saveTreeComment(NewTreeCommentEntity(
            treeId = currTreeId,
            authorId = Random().nextInt(arr.size).toLong(),
            text = commText,
            createTime = System.currentTimeMillis().toString()
        ))
    }

}