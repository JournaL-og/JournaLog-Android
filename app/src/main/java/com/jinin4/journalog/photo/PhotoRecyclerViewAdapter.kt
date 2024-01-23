package com.jinin4.journalog.photo


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jinin4.journalog.R
import com.jinin4.journalog.databinding.ItemPhotoBinding
import com.jinin4.journalog.db.photo.PhotoEntity


// 이지윤 작성 - 24.01.22
class PhotoRecyclerViewAdapter(private val photoList : ArrayList<PhotoEntity>) : RecyclerView.Adapter<PhotoRecyclerViewAdapter.PhotoViewHolder>() {
    var context: Context? = null
    inner class PhotoViewHolder(binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        val imagePhoto: ImageView = binding.imagePhoto
        val root = binding.root

        init {
            imagePhoto.setOnClickListener {
                val position = bindingAdapterPosition // 현재 뷰 홀더의 위치 가져오기
                if (position != RecyclerView.NO_POSITION) {

                    // 여기서 이미지 클릭 이벤트 처리
                    val dialogView = LayoutInflater.from(it.context)
                        .inflate(R.layout.fragment_photo_dialog, null)
                    val dlg = AlertDialog.Builder(it.context)
                    val ivPic: ImageView = dialogView.findViewById<ImageView>(R.id.ivPic)
                    Glide.with(it.context)
                        .load(photoList[position].photo_url)
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
        return photoList.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.imagePhoto.setImageResource(R.drawable.test)

    }

    override fun getItemId(i: Int): Long {
        return photoList[i].photo_id?.toLong() ?: 0L
    }

}