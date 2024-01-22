package com.jinin4.journalog.photo

import android.provider.ContactsContract.Contacts.Photo
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.jinin4.journalog.R
import com.jinin4.journalog.databinding.ItemPhotoBinding
import com.jinin4.journalog.db.photo.PhotoEntity

// 이지윤 작성 - 24.01.22
class PhotoRecyclerViewAdapter(private val photoList : ArrayList<PhotoEntity>) : RecyclerView.Adapter<PhotoRecyclerViewAdapter.PhotoViewHolder>() {
    inner class PhotoViewHolder(binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        val imagePhoto: ImageView = binding.imagePhoto
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding: ItemPhotoBinding = ItemPhotoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.imagePhoto.setImageResource(R.drawable.test)

    }
}