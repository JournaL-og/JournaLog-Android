package com.jinin4.journalog.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.jinin4.journalog.databinding.FragmentPhotoBinding
import com.jinin4.journalog.db.memo.MemoDao
import com.jinin4.journalog.db.memo.MemoEntity
import com.jinin4.journalog.db.photo.PhotoDao
import com.jinin4.journalog.db.photo.PhotoEntity
import com.jinin4.journalog.utils.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.developia.todolist.db.JournaLogDatabase

//이상원 - 24.01.19
//이지윤 수정 - 24.01.22


class PhotoFragment : BaseFragment() {

    private lateinit var binding: FragmentPhotoBinding
    private lateinit var adapter: PhotoRecyclerViewAdapter
    lateinit var db: JournaLogDatabase
    lateinit var photoDao: PhotoDao
    lateinit var memoDao: MemoDao

    private var photoList: MutableList<PhotoEntity> =  mutableListOf<PhotoEntity>()
    private var memoList: MutableList<MemoEntity> =  mutableListOf<MemoEntity>()

    private var isDataLoaded = false // 데이터 로드 여부를 확인하는 플래그

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPhotoBinding.inflate(inflater, container, false)

        db = JournaLogDatabase.getInstance(binding.root.context)!!
        photoDao = db.getPhotoDao()
        memoDao = db.getMemoDao()

        if (!isDataLoaded) {
            getAllPhotoList()
        }

        return binding.root
    }

    private fun getAllPhotoList() {
        CoroutineScope(Dispatchers.IO).launch {
            // 데이터베이스 작업 수행
            val photos = photoDao.getAllPhotos()

            val countText:TextView = binding.countText
            countText.text = photos.size.toString() + "개"

            photos.forEach { photo ->
                photoList.add(photo)
                val memo = memoDao.getMemoById(photo.memo_id)
                memo?.let {
                    memoList.add(it)
                }
            }

            withContext(Dispatchers.Main) {
                // UI 업데이트 및 리사이클러뷰 설정
                setRecyclerView()
            }
        }
}

    private fun setRecyclerView() {
        requireActivity().runOnUiThread {
            adapter = PhotoRecyclerViewAdapter(binding.root.context, photoList, memoList , parentFragmentManager) // ❷ 어댑터 객체 할당
            binding.imageRecyclerView.adapter = adapter // 리사이클러뷰 어댑터로 위에서 만든 어댑터 설정
            binding.imageRecyclerView.layoutManager = GridLayoutManager(binding.root.context,3) // 레이아웃 매니저 설정
        }
    }
}