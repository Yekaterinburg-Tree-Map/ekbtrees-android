package ru.ekbtrees.treemap.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.TreePhotoItemBinding

class TreePhotosAdapter(private val onItemClick: ((String) -> Unit)? = null) :
    ListAdapter<String, TreePhotosAdapter.ViewHolder>(PhotoItemDiffCallback()) {

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding = TreePhotoItemBinding.bind(view)

        fun bind(photoUri: String, onItemClick: ((String) -> Unit)? = null) {
            binding.treePhoto.setOnClickListener {
                onItemClick?.invoke(photoUri)
            }
            Glide.with(view).load(photoUri).into(binding.treePhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tree_photo_item, parent, false)

        return ViewHolder(view = view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photoUri = getItem(position), onItemClick)
    }

    class PhotoItemDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
