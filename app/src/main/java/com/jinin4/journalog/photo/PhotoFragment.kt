package com.jinin4.journalog.photo

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.jinin4.journalog.databinding.FragmentPhotoBinding
import com.jinin4.journalog.db.photo.PhotoDao
import com.jinin4.journalog.firebase.storage.FirebaseFileManager
import net.developia.todolist.db.JournaLogDatabase

//이상원 - 24.01.19
//이지윤 수정 - 24.01.22

object PhotoDataHolder {
    var uriList: MutableList<String>? = null
    var isDataChanged = false
}


class PhotoFragment : Fragment() {

    private lateinit var binding: FragmentPhotoBinding
    private lateinit var adapter: PhotoRecyclerViewAdapter
    lateinit var db: JournaLogDatabase
    lateinit var photoDao: PhotoDao
    private var uriList: MutableList<String> =  mutableListOf<String>()
    private var isDataLoaded = false // 데이터 로드 여부를 확인하는 플래그

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPhotoBinding.inflate(inflater, container, false)

        db = JournaLogDatabase.getInstance(binding.root.context)!!
        photoDao = db.getPhotoDao()

        if (!isDataLoaded) {
            getAllPhotoList()
        }

        return binding.root
    }

    @SuppressLint("HardwareIds")
    private fun getAllPhotoList() {

        if (PhotoDataHolder.uriList == null || PhotoDataHolder.isDataChanged) {
            val androidID: String = (Settings.Secure.getString(
                context?.contentResolver,
                Settings.Secure.ANDROID_ID
            ) + "/thumbnail") ?: "default"

            FirebaseFileManager.loadImageFromFirebase(androidID) { uri ->
                PhotoDataHolder.uriList = uri
                uriList = uri
                Log.d("FirebaseFileManager","uri found: ${uri}, uri size: ${uri.size}")
                setRecyclerView()
            }
        } else {
            uriList = PhotoDataHolder.uriList!!
            setRecyclerView()
        }
}

    private fun setRecyclerView() {
        requireActivity().runOnUiThread {
            adapter = PhotoRecyclerViewAdapter(binding.root.context, uriList, parentFragmentManager) // ❷ 어댑터 객체 할당
            binding.imageRecyclerView.adapter = adapter // 리사이클러뷰 어댑터로 위에서 만든 어댑터 설정
            binding.imageRecyclerView.layoutManager = GridLayoutManager(binding.root.context,3) // 레이아웃 매니저 설정
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("FirebaseFileManager", "onResume")
        Log.d("FirebaseFileManager onResume isDataChanged", PhotoDataHolder.isDataChanged.toString())
        if (PhotoDataHolder.isDataChanged) {
            getAllPhotoList() // 데이터를 다시 불러옵니다
            PhotoDataHolder.isDataChanged = false
            Log.d("FirebaseFileManager onResume isDataChanged2", PhotoDataHolder.isDataChanged.toString())
        }
    }
}