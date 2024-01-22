package com.jinin4.journalog.photo


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jinin4.journalog.databinding.ItemPhotoBinding

// 이지윤 작성 - 24.01.22
class PhotoRecyclerViewAdapter : RecyclerView.Adapter<PhotoRecyclerViewAdapter.PhotoViewHolder>() {
    inner class PhotoViewHolder(binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {

//        val tv_title = binding.tvTitle
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding: ItemPhotoBinding = ItemPhotoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
//        holder.tv_title.text = "하이하이 테스트입니다."
    }
}