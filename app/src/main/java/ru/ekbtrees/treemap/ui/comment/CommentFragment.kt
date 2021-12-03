package ru.ekbtrees.treemap.ui.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.FragmentCommentBinding

@AndroidEntryPoint
class CommentFragment : Fragment() {
    private lateinit var binding: FragmentCommentBinding
    private val adapter = CommentRecyclerAdapter()
    private val testComment = CommentView("Me","This is comment")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener{
            activity?.onBackPressed()
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.sendButton.setOnClickListener{
            if(!binding.editTextTextMultiLine.text.toString().equals("")) {
                adapter.commentList.add(CommentView("Me", binding.editTextTextMultiLine.text.toString()))
                adapter.submitList(adapter.commentList)
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
    }
}