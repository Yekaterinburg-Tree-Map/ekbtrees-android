package ru.ekbtrees.treemap.ui.comment

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
class CommentFragmentViewModel @Inject constructor(
    private val interactor: CommentInteractor
) : BaseViewModel<CommentContract.CommentEvent, CommentContract.CommentState, CommentContract.CommentEffect>() {

    override fun createInitialState(): CommentContract.CommentState {
        return CommentContract.CommentState.Idle
    }

    val commentList = ArrayList<CommentView>()
    private lateinit var currTreeId: String

    fun provideTreeId(treeId: String) {
        currTreeId = treeId
    }

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is CommentContract.CommentEvent.Load -> {
                viewModelScope.launch {
                    setState(CommentContract.CommentState.Loading)
                    try {
                        val treeComments = interactor.getTreeCommentBy(currTreeId)
                        setState(CommentContract.CommentState.Loaded(treeComments.toList()))
                    } catch (e: Exception) {
                        setState((CommentContract.CommentState.Error))
                    }
                }
            }
            is CommentContract.CommentEvent.SendCommentButtonClicked -> {
                viewModelScope.launch {
                    setState(CommentContract.CommentState.Loading)
                    saveNewComment(event.text)
                    setEvent(CommentContract.CommentEvent.Load(currTreeId))
                }
            }

        }
    }

    private suspend fun saveNewComment(commText: String){
        val arr = arrayOf(Constants.UsersNames.ME.name, Constants.UsersNames.ANOTHER_USER.name)
        val randIndex = Random().nextInt(arr.size)
        val result = interactor.saveTreeComment(
            getNewTreeCommentUIModel(commText, arr[randIndex]).toNewCommentEntity())
        setEffect {
            if (result is UploadResult.Success) {
                CommentContract.CommentEffect.BackOnBackStack
            } else {
                CommentContract.CommentEffect.ShowErrorMessage
            }
        }
        commentList.add(CommentView(arr[randIndex], commText))
    }

    private fun getNewTreeCommentUIModel(text: String, authorId: String) : NewTreeCommentUIModel {
        return NewTreeCommentUIModel(
            treeId = currTreeId,
            authorId = authorId,
            text = text,
            createTime = System.currentTimeMillis().toString(),
            updateTime = null
        )
    }

}