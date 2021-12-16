package ru.ekbtrees.treemap.ui.comment

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.ui.mvi.base.BaseViewModel
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.contract.CommentContract
import ru.ekbtrees.treemap.ui.mvi.contract.TreeDetailContract
import javax.inject.Inject
import java.util.ArrayList

@HiltViewModel
class CommentFragmentViewModel @Inject constructor(
    private val interactor: TreesInteractor
) : BaseViewModel<CommentContract.CommentEvent, CommentContract.CommentState, CommentContract.CommentEffect>() {
    override fun createInitialState(): CommentContract.CommentState {
        return CommentContract.CommentState.Idle
    }
    private val commentList = ArrayList<CommentView>()



    override fun handleEvent(event: UiEvent) {

        when(event) {
            is CommentContract.CommentEvent.SendCommentButtonClicked ->{
                viewModelScope.launch {
                    commentList.add(CommentView("Me", event.text))
                    setState(CommentContract.CommentState.Loaded(commentList))
                }
            }

        }
    }
}