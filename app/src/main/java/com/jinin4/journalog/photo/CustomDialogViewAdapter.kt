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

class CustomDialogViewAdapter(private val context: Context, private val photoList: MutableList<PhotoEntity>) :
    RecyclerView.Adapter<CustomDialogViewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagePhoto: ImageView = view.findViewById(R.id.image_dialog_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dialog_photo, parent, false)  // 올바른 아이템 레이아웃 리소스 사용
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri = photoList[position].photo_uri.toUri()
        Glide.with(context)
            .load(imageUri)
            .into(holder.imagePhoto)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }
}