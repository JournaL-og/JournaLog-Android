package com.jinin4.journalog.photo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jinin4.journalog.R
import com.jinin4.journalog.db.photo.PhotoEntity

// 이지윤 이미지 슬라이더 구현 - 24.01.26
class ImageSliderAdapter(private val context: Context, private val photoList: MutableList<PhotoEntity>) :
    RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagePhoto: ImageView = view.findViewById(R.id.image_dialog_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dialog_photo, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = photoList[position].photo_uri.toUri()
        Glide.with(context)
            .load(imageUri)
            .into(holder.imagePhoto)
    }

    override fun getItemCount(): Int = photoList.size
}
