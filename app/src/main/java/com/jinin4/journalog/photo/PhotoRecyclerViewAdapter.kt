package com.jinin4.journalog.photo


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jinin4.journalog.R
import com.jinin4.journalog.databinding.ItemPhotoBinding


// 이지윤 작성 - 24.01.22
class PhotoRecyclerViewAdapter(private val context: Context, private val uriList : MutableList<String>)
    : RecyclerView.Adapter<PhotoRecyclerViewAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        val imagePhoto: ImageView = binding.imagePhoto
        val root = binding.root

        init {
            imagePhoto.setImageResource(R.drawable.skeleton_img)
            // Adapter 초기화 시점에 이미지를 미리 로드합니다.
            preloadImages()

            imagePhoto.setOnClickListener {
                val position = bindingAdapterPosition // 현재 뷰 홀더의 위치 가져오기
                if (position != RecyclerView.NO_POSITION) {

                    // 여기서 이미지 클릭 이벤트 처리
                    val dialogView = LayoutInflater.from(it.context)
                        .inflate(R.layout.fragment_photo_dialog, null)
                    val dlg = AlertDialog.Builder(it.context)
                    val ivPic: ImageView = dialogView.findViewById<ImageView>(R.id.ivPic)
                    Log.d("FirebaseImageLoad","uriList[position]: ${uriList[position]}, ${position}")
                    Glide.with(it.context)
                        .load(uriList[position])
                        .placeholder(R.drawable.skeleton_img)
                        .into(ivPic)

                    dlg.setTitle("큰 이미지")
//                dlg.setIcon(R.drawable.ic_launcher)
                    dlg.setView(dialogView)
                    dlg.setNegativeButton("닫기", null)
                    dlg.show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding: ItemPhotoBinding = ItemPhotoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return uriList.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val imageUrl = uriList[position]

        holder.apply {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .override(150, 150)
                .placeholder(R.drawable.skeleton_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(holder.imagePhoto)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun preloadImages() {
        uriList.forEach { imageUrl ->
            Glide.with(context)
                .load(imageUrl)
                .override(150, 150)
                .placeholder(R.drawable.skeleton_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .preload()
        }
    }

}


