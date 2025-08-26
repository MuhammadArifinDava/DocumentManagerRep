package com.epic.documentmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.epic.documentmanager.R

class DocumentImageAdapter(
    private var images: List<DocumentImage>,
    private val onImageClick: (DocumentImage, Int) -> Unit,
    private val onDeleteClick: (DocumentImage, Int) -> Unit,
    private val canDelete: Boolean = true
) : RecyclerView.Adapter<DocumentImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val tvImageName: TextView = itemView.findViewById(R.id.tvImageName)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)

        init {
            imageView.setOnClickListener {
                onImageClick(images[adapterPosition], adapterPosition)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(images[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]

        holder.tvImageName.text = image.name

        // Load image using Glide
        if (image.uri != null) {
            // Local image
            Glide.with(holder.imageView.context)
                .load(image.uri)
                .centerCrop()
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .into(holder.imageView)
        } else if (image.url.isNotEmpty()) {
            // Remote image
            Glide.with(holder.imageView.context)
                .load(image.url)
                .centerCrop()
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_error)
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.ic_image_placeholder)
        }

        // Show/hide delete button
        holder.btnDelete.visibility = if (canDelete) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = images.size

    fun updateImages(newImages: List<DocumentImage>) {
        images = newImages
        notifyDataSetChanged()
    }

    fun removeImage(position: Int) {
        if (position < images.size) {
            val mutableImages = images.toMutableList()
            mutableImages.removeAt(position)
            images = mutableImages
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, images.size)
        }
    }

    fun addImage(image: DocumentImage) {
        val mutableImages = images.toMutableList()
        mutableImages.add(image)
        images = mutableImages
        notifyItemInserted(images.size - 1)
    }
}

data class DocumentImage(
    val name: String,
    val uri: android.net.Uri? = null,
    val url: String = "",
    val type: String = "" // KTP, KK, NPWP, etc.
)