package ru.ekbtrees.treemap.ui.common

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.ekbtrees.treemap.R

class TreePhotosAdapter(photosUris: List<Uri>, private val onItemClick: ((Uri) -> Unit)? = null) :
    RecyclerView.Adapter<TreePhotosAdapter.ViewHolder>() {

    private val _photoUris: MutableList<Uri> = photosUris.toMutableList()

    fun addPhoto(photoUri: Uri) {
        _photoUris.add(photoUri)
        notifyItemInserted(_photoUris.size)
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private var image: ImageView = view.findViewById(R.id.tree_photo)

        fun bind(photoUri: Uri, onItemClick: ((Uri) -> Unit)? = null) {
            image.setOnClickListener {
                onItemClick?.invoke(photoUri)
            }
            Glide.with(view).load(photoUri).into(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tree_photo_item, parent, false)

        return ViewHolder(view = view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photoUri = _photoUris[position], onItemClick)
    }

    override fun getItemCount(): Int = _photoUris.size
}