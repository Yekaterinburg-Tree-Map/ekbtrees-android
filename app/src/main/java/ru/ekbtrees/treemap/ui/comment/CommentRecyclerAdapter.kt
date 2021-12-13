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




class CommentRecyclerAdapter: ListAdapter<CommentView, CommentRecyclerAdapter.CommentHolder>(ItemComprator()) {


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

    class ItemComprator : DiffUtil.ItemCallback<CommentView>(){
        override fun areItemsTheSame(oldItem: CommentView, newItem: CommentView): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CommentView, newItem: CommentView): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        return CommentHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.bind(getItem(position))
    }

}