package ru.ekbtrees.treemap.ui.comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.CommentItemBinding

class CommentRecyclerAdapter: RecyclerView.Adapter<CommentRecyclerAdapter.CommentHolder>() {
    val commentList = listOf<CommentView>(
        CommentView("This is first comment"),
        CommentView("This is second comment"),
        CommentView("This is third comment"),
    )
    //ArrayList<CommentView>() будем использовать, когда комментарии будут нефиктивными и заполняться с помощью addComment


    class CommentHolder(commentItem: View): RecyclerView.ViewHolder(commentItem) {
        val binding = CommentItemBinding.bind(commentItem)
        fun bind(comm: CommentView) = with(binding){
            commentTextView.text = comm.commText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentHolder(view)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.bind(commentList[position])
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    /** Будет использоваться для заполнения листа комментариев **/
//    fun addComment(comm: CommentView){
//        commentList.add(comm)
//        notifyDataSetChanged()
//    }

}