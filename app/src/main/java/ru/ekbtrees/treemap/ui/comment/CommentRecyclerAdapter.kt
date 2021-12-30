package ru.ekbtrees.treemap.ui.comment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.CommentItemBinding
import ru.ekbtrees.treemap.databinding.CommentItemAnotherUserBinding
import kotlin.random.Random
import androidx.annotation.NonNull

class CommentRecyclerAdapter: ListAdapter<CommentView, RecyclerView.ViewHolder>(ItemComprator()) {

    class CommentHolder(private val binding: CommentItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(comm: CommentView) = with(binding){
            commentTextView.text = comm.commText
            userName.text = comm.userName
        }
        companion object{
            fun create(parent: ViewGroup): CommentHolder{
                return CommentHolder(CommentItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    class AnotherUserCommentHolder(private val binding: CommentItemAnotherUserBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(comm: CommentView) = with(binding){
            commentTextView.text = comm.commText
            userName.text = comm.userName
        }
        companion object{
            fun create(parent: ViewGroup): AnotherUserCommentHolder{
                return AnotherUserCommentHolder(CommentItemAnotherUserBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    class ItemComprator : DiffUtil.ItemCallback<CommentView>(){
        override fun areItemsTheSame(oldItem: CommentView, newItem: CommentView): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CommentView, newItem: CommentView): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is AnotherUserCommentHolder -> holder.bind(getItem(position))
            is CommentHolder -> holder.bind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            return AnotherUserCommentHolder.create(parent)
        } else {
            return CommentHolder.create(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val name = getItem(position).userName
        if (name == Constants.UsersNames.ANOTHER_USER.name){
            return 0
        } else{
            return 1
        }
    }
}

