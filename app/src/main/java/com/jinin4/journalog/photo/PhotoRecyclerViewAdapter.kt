package com.jinin4.journalog.photo


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jinin4.journalog.R
import com.jinin4.journalog.databinding.ItemPhotoBinding
import com.jinin4.journalog.db.memo.MemoEntity
import com.jinin4.journalog.db.photo.PhotoEntity


// 이지윤 작성 - 24.01.22
class PhotoRecyclerViewAdapter(private val context: Context, private val photoList: MutableList<PhotoEntity>, private val memoList: MutableList<MemoEntity>, private val fragmentManager: FragmentManager)
//class PhotoRecyclerViewAdapter(private val context: Context, private val uriList : MutableList<String>,private val fragmentManager: FragmentManager)
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
                    // 이지윤 이미지 슬라이더 구현 - 24.01.26
                    val dialogFragment = CustomPhotoDialogFragment(photoList,memoList, position)
                    dialogFragment.show(fragmentManager, "customDialog")

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
        return photoList.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val imageUri = photoList[position].photo_uri.toUri()

        holder.apply {
            Glide.with(holder.itemView.context)
                .load(imageUri)
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
        photoList.forEach { photo ->
            Glide.with(context)
                .load(photo.photo_uri.toUri())
                .override(150, 150)
                .placeholder(R.drawable.skeleton_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .preload()
        }
    }

}


