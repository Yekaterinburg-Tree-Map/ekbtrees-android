package ru.ekbtrees.treemap.ui.comment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.ActivityMainBinding
import ru.ekbtrees.treemap.databinding.FragmentCommentBinding
import ru.ekbtrees.treemap.ui.comment.CommentFragmentArgs
import ru.ekbtrees.treemap.ui.comment.CommentRecyclerAdapter

@AndroidEntryPoint
class CommentActivity : AppCompatActivity() {
    lateinit var binding: FragmentCommentBinding
    private val adapter = CommentRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@CommentActivity)
            recyclerView.adapter = adapter
        }
    }
}