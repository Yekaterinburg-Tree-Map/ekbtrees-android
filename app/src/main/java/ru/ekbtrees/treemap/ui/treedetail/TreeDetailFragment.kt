package ru.ekbtrees.treemap.ui.treedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.FragmentTreeDetailBinding
import ru.ekbtrees.treemap.ui.mvi.contract.TreeDetailContract

private const val TAG = "TreeDetailFragment"
private const val ARG_PARAM1 = "TreeId"

/**
 * Фрагмент детализауии дерева.
 * */
@AndroidEntryPoint
class TreeDetailFragment : Fragment() {
    private var treeId: String? = null

    private val treeDetailViewModel: TreeDetailViewModel by viewModels()

    private lateinit var binding: FragmentTreeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            treeId = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTreeDetailBinding.inflate(inflater, container, false)
        binding.textView.text = treeId
        return binding.root
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            treeDetailViewModel.uiState.collect { viewState ->
                when (viewState) {
                    is TreeDetailContract.TreeDetailViewState.Idle -> {
                    }
                    is TreeDetailContract.TreeDetailViewState.TreeDetailLoadingState -> {
                        // Show progress bar
                    }
                    is TreeDetailContract.TreeDetailViewState.TreeDetailLoadedState -> {
                        // Show tree detail data
                    }
                    is TreeDetailContract.TreeDetailViewState.TreeDetailErrorState -> {
                        // Show error
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(treeId: String) =
            TreeDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, treeId)
                }
            }
    }
}