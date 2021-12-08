package ru.ekbtrees.treemap.ui.comment

import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.ekbtrees.treemap.databinding.FragmentCommentBinding
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.ui.mvi.base.BaseViewModel
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.contract.CommentFragmentContract
import ru.ekbtrees.treemap.ui.mvi.contract.TreeDetailContract
import javax.inject.Inject
import androidx.fragment.app.Fragment
import android.view.LayoutInflater

@HiltViewModel
class CommentFragmentViewModel @Inject constructor(
    private val interactor: TreesInteractor
) : BaseViewModel<CommentFragmentContract.CommentFragmentEvent, CommentFragmentContract.CommentFragmentState, CommentFragmentContract.CommentFragmentEffect>() {
    override fun createInitialState(): CommentFragmentContract.CommentFragmentState {
        return CommentFragmentContract.CommentFragmentState.Idle
    }

    fun addComment(binding: FragmentCommentBinding, adapter: CommentRecyclerAdapter) {
            if(!binding.editTextTextMultiLine.text.toString().equals("")) {
                adapter.commentList.add(CommentView("Me", binding.editTextTextMultiLine.text.toString()))
                adapter.submitList(adapter.commentList)
                binding.editTextTextMultiLine.text.clear()
            }

    }

    override fun handleEvent(event: UiEvent) {
    }
}