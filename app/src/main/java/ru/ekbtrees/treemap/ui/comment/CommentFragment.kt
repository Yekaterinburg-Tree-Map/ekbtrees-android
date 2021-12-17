package ru.ekbtrees.treemap.ui.comment

import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.databinding.FragmentCommentBinding
import ru.ekbtrees.treemap.ui.mvi.contract.CommentContract

@AndroidEntryPoint
class CommentFragment : Fragment() {
    private lateinit var binding: FragmentCommentBinding
    private val adapter = CommentRecyclerAdapter()
    private val viewModel: CommentFragmentViewModel by viewModels()

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
            if (!binding.editTextTextMultiLine.text.toString().equals("")) {
                viewModel.handleEvent(CommentContract.CommentEvent.SendCommentButtonClicked(binding.editTextTextMultiLine.text.toString()))
                binding.editTextTextMultiLine.text.clear()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: CommentFragmentArgs by navArgs()
        val treeId = args.treeId
        binding.treeIdView.text = treeId
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.uiState.collect { newState ->
                when (newState) {
                    is CommentContract.CommentState.Loaded -> {
                        adapter.submitList(newState.comments)
                    }
                }

            }
        }
    }
}