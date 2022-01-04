package ru.ekbtrees.treemap.ui.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.databinding.FragmentCommentBinding
import ru.ekbtrees.treemap.ui.mappers.toCommentView
import ru.ekbtrees.treemap.ui.mvi.contract.CommentContract

@AndroidEntryPoint
class CommentFragment : Fragment() {
    private lateinit var binding: FragmentCommentBinding
    private val adapter = CommentRecyclerAdapter()
    private val viewModel: CommentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.sendButton.setOnClickListener {
            if (binding.editTextTextMultiLine.text.toString().isNotEmpty()) {
                viewModel.handleEvent(CommentContract.CommentEvent.SendCommentButtonClicked(binding.editTextTextMultiLine.text.toString()))
                binding.editTextTextMultiLine.text.clear()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handleEvent(CommentContract.CommentEvent.Load)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { newState ->
                cleanUI()
                when (newState) {
                    is CommentContract.CommentState.Loaded -> {
                        adapter.submitList(newState.comments.map { commentEntity -> commentEntity.toCommentView() })
                    }
                    is CommentContract.CommentState.Error -> {
                        onErrorState()
                    }
                    is CommentContract.CommentState.Idle -> {
                    }
                    is CommentContract.CommentState.Loading -> {
                        onDataLoadingState()
                    }
                }

            }
        }
    }

    private fun cleanUI() {
        binding.loadingContent.visibility = View.GONE
        binding.errorContent.visibility = View.GONE
    }

    private fun onDataLoadingState() {
        binding.loadingContent.visibility = View.VISIBLE
    }

    private fun onErrorState() {
        binding.errorContent.visibility = View.VISIBLE
    }
}