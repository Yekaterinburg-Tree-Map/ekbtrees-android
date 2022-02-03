package ru.ekbtrees.treemap.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.Shimmer.Direction.LEFT_TO_RIGHT
import com.facebook.shimmer.ShimmerDrawable
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.TreePhotoItemBinding
import ru.ekbtrees.treemap.ui.model.PhotoUiModel

private object ViewType {
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
            binding.deleteButton.apply {
                visibility = View.VISIBLE
                setOnClickListener {  }
            }
            val shimmer = Shimmer.AlphaHighlightBuilder().apply {
                setDuration(1000)
                setBaseAlpha(0.7f)
                setHighlightAlpha(0.6f)
                setDirection(LEFT_TO_RIGHT)
                setAutoStart(true)
            }.build()
            val shimmerDrawable = ShimmerDrawable().apply {
                setShimmer(shimmer)
            }
            val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
            Glide.with(view).load(photoUri)
                .placeholder(shimmerDrawable)
                .transition(DrawableTransitionOptions.withCrossFade(factory))
                .into(binding.treePhoto)
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
                is PhotoUiModel.Photo -> {
                    newItem is PhotoUiModel.Photo
                }
                is PhotoUiModel.Uploading -> {
                    newItem is PhotoUiModel.Uploading
                }
                PhotoUiModel.Error -> {
                    newItem is PhotoUiModel.Error
                }
            }
        }
    }
}
