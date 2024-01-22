package com.jinin4.journalog.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jinin4.journalog.databinding.FragmentPhotoBinding
import com.jinin4.journalog.databinding.FragmentTestBinding
import com.jinin4.journalog.db.memo.MemoDao
import com.jinin4.journalog.db.memo.MemoEntity
import com.jinin4.journalog.db.photo.PhotoDao
import com.jinin4.journalog.db.photo.PhotoEntity
import com.jinin4.journalog.firebase.storage.FirebaseFileManager
import net.developia.todolist.db.JournaLogDatabase
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//이상원 - 24.01.19
//이지윤 수정 - 24.01.22
class PhotoFragment : Fragment() {

    private lateinit var binding: FragmentPhotoBinding
    private lateinit var adapter: PhotoRecyclerViewAdapter

    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = firebaseStorage.reference

    lateinit var db: JournaLogDatabase
    lateinit var photoDao: PhotoDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPhotoBinding.inflate(inflater, container, false)

        db = JournaLogDatabase.getInstance(binding.root.context)!!
        photoDao = db.getPhotoDao()

        binding.btnCallDb.setOnClickListener{

            // sqlite 에는 Date 타입이 없어 스트링으로 넣어야 함!! 참고하세요
            val photoEntity = PhotoEntity(null, 1,"photo/test.png")

            Thread{
                photoDao.insertPhoto(photoEntity)
                requireActivity().runOnUiThread{
                    Toast.makeText(binding.root.context, "추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }.start()
        }

        getAllPhotoList()
        return binding.root
    }

    private fun getAllPhotoList() {
        Thread {
            setRecyclerView()
        }.start()
    }

    private fun setRecyclerView() {
        requireActivity().runOnUiThread {
            adapter = PhotoRecyclerViewAdapter() // ❷ 어댑터 객체 할당
            binding.imageRecyclerView.adapter = adapter // 리사이클러뷰 어댑터로 위에서 만든 어댑터 설정
            binding.imageRecyclerView.layoutManager = GridLayoutManager(binding.root.context,3) // 레이아웃 매니저 설정
        }
    }
}