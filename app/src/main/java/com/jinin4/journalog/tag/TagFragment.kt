package com.jinin4.journalog.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jinin4.journalog.databinding.FragmentTagBinding
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
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//이상원 - 24.01.19
//최성혁 - 수정 24.01.22
class TagFragment : BaseFragment() {

    private lateinit var binding: FragmentTagBinding
    private lateinit var db: JournaLogDatabase
    private lateinit var photoDao: PhotoDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTagBinding.inflate(inflater, container, false)
        db = JournaLogDatabase.getInstance(binding.root.context)!!
        photoDao = db.getPhotoDao()

        getAllMemoList()
        setupButtonListeners()

        return binding.root
    }

    private fun setupButtonListeners() {
        binding.entireBtn.setOnClickListener { getAllMemoList() }

        // getMemoByColorId
        binding.redBtn.setOnClickListener { filterMemosByColorId(1) }
        binding.orangeBtn.setOnClickListener { filterMemosByColorId(2) }
        binding.yellowBtn.setOnClickListener { filterMemosByColorId(3) }
        binding.greenBtn.setOnClickListener { filterMemosByColorId(4) }
        binding.blueBtn.setOnClickListener { filterMemosByColorId(5) }
        binding.indigoBtn.setOnClickListener { filterMemosByColorId(6) }
        binding.puppleBtn.setOnClickListener { filterMemosByColorId(7) }
    }

    private fun getAllMemoList() {
        CoroutineScope(Dispatchers.IO).launch {
            val memos = db.getMemoDao().getAllMemo()
            val imageMap = getImageMap(memos)
            withContext(Dispatchers.Main) {
                setRecyclerView(memos, imageMap)
            }
        }
    }

    private fun filterMemosByColorId(colorId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val filteredMemos = db.getMemoDao().getMemosByColorId(colorId)
            val imageMap = getImageMap(filteredMemos)
            withContext(Dispatchers.Main) {
                setRecyclerView(filteredMemos, imageMap)
            }
        }
    }

    private fun setRecyclerView(memoList: List<MemoEntity>, imageMap: Map<Int, List<PhotoEntity>>) {
        requireActivity().runOnUiThread {
            val adapter = TagMemoRecyclerViewAdapter(ArrayList(memoList), imageMap)
            binding.tagRecyclerView.adapter = adapter
            binding.tagRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        }
    }

    private suspend fun getImageMap(memos: List<MemoEntity>): Map<Int, List<PhotoEntity>> {
        return memos.mapNotNull { memo ->
            memo.memo_id?.let { id ->
                val photos = photoDao.getPhotoByMemoId(id)
                id to photos
            }
        }.toMap()
    }


}