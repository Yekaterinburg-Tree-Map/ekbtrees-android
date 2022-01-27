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
import ru.ekbtrees.treemap.ui.model.PhotoUiModel

private object ViewType {
    const val Loading = 0
    const val Uploading = 1
    const val Photo = 2
    const val Error = 3
}

class TreePhotosAdapter(private val onItemClick: ((String) -> Unit)? = null) :
    ListAdapter<PhotoUiModel, RecyclerView.ViewHolder>(PhotoItemDiffCallback()) {

    private abstract class BaseViewHolder(protected val view: View) :
        RecyclerView.ViewHolder(view) {

        protected val binding = TreePhotoItemBinding.bind(view)
    }

    private class PhotoViewHolder(view: View) : BaseViewHolder(view) {
        fun bind(photoUri: String, onItemClick: ((String) -> Unit)? = null) {
            binding.treePhoto.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    onItemClick?.invoke(photoUri)
                }
            }
            Glide.with(view).load(photoUri).into(binding.treePhoto)
        }
    }

    private class LoadingViewHolder(view: View) : BaseViewHolder(view) {
        fun bind() {
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private class UploadingViewHolder(view: View) : BaseViewHolder(view) {
        fun bind() {
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private class ErrorViewHolder(view: View) : BaseViewHolder(view) {
        fun bind() {
            binding.errorText.visibility = View.VISIBLE
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            PhotoUiModel.Error -> {
                ViewType.Error
            }
            PhotoUiModel.Loading -> {
                ViewType.Loading
            }
            is PhotoUiModel.Photo -> {
                ViewType.Photo
            }
            is PhotoUiModel.Uploading -> {
                ViewType.Uploading
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tree_photo_item, parent, false)

        return when (viewType) {
            ViewType.Loading -> {
                LoadingViewHolder(view = view)
            }
            ViewType.Uploading -> {
                UploadingViewHolder(view = view)
            }
            ViewType.Photo -> {
                PhotoViewHolder(view = view)
            }
            ViewType.Error -> {
                ErrorViewHolder(view = view)
            }
            else -> {
                error("Unknown viewType: $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder.itemViewType) {
            ViewType.Loading -> {
                (holder as LoadingViewHolder).bind()
            }
            ViewType.Uploading -> {
                (holder as UploadingViewHolder).bind()
            }
            ViewType.Photo -> {
                val photoItem = item as PhotoUiModel.Photo
                (holder as PhotoViewHolder).bind(
                    photoUri = photoItem.photoUrl,
                    onItemClick = onItemClick
                )
            }
            ViewType.Error -> {
                (holder as ErrorViewHolder).bind()
            }
        }
    }

    class PhotoItemDiffCallback : DiffUtil.ItemCallback<PhotoUiModel>() {
        override fun areItemsTheSame(oldItem: PhotoUiModel, newItem: PhotoUiModel): Boolean {
            when (oldItem) {
                PhotoUiModel.Error -> {
                    return newItem is PhotoUiModel.Error
                }
                PhotoUiModel.Loading -> {
                    return oldItem is PhotoUiModel.Loading
                }
                is PhotoUiModel.Photo -> {
                    if (newItem is PhotoUiModel.Photo) {
                        return oldItem.photoUrl == newItem.photoUrl
                    }
                    return false
                }
                is PhotoUiModel.Uploading -> {
                    if (newItem is PhotoUiModel.Uploading) {
                        return oldItem.filePath == newItem.filePath
                    }
                    return false
                }
            }
        }

        override fun areContentsTheSame(oldItem: PhotoUiModel, newItem: PhotoUiModel): Boolean {
            return when (oldItem) {
                PhotoUiModel.Error -> {
                    newItem is PhotoUiModel.Error
                }
                PhotoUiModel.Loading -> {
                    newItem is PhotoUiModel.Loading
                }
                is PhotoUiModel.Photo -> {
                    newItem is PhotoUiModel.Photo
                }
                is PhotoUiModel.Uploading -> {
                    newItem is PhotoUiModel.Uploading
                }
            }
        }
    }
}
