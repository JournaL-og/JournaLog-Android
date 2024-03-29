package com.jinin4.journalog.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jinin4.journalog.Preference
import com.jinin4.journalog.calendar.MemoInsertCallback
import com.jinin4.journalog.dataStore
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
import org.threeten.bp.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//이상원 - 24.01.19
//최성혁 - 수정 24.01.22
class TagFragment : BaseFragment(),MemoInsertCallback {

    private lateinit var binding: FragmentTagBinding
    private lateinit var db: JournaLogDatabase
    private lateinit var photoDao: PhotoDao
    private var memoSortOrder: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTagBinding.inflate(inflater, container, false)
        db = JournaLogDatabase.getInstance(binding.root.context)!!
        photoDao = db.getPhotoDao()

        viewLifecycleOwner.lifecycleScope.launch {
            context?.dataStore?.data?.collect { preferences ->
                memoSortOrder = when (preferences.diarySortOrder) {
                    Preference.SortOrder.DESCENDING -> true
                    else -> false
                }
            }
        }

        getAllMemoList()
        setupButtonListeners()

        return binding.root
    }

    override fun onMemoInserted() {
        getAllMemoList()
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
            val memos = when (memoSortOrder) {
                true -> db.getMemoDao().getAllMemo()
                else -> db.getMemoDao().getAllMemoAsc()
            }

            val imageMap = getImageMap(memos)
            withContext(Dispatchers.Main) {
                setRecyclerView(memos, imageMap)
            }
        }
    }

    private fun filterMemosByColorId(colorId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val filteredMemos = when (memoSortOrder) {
                true -> db.getMemoDao().getMemosByColorId(colorId)
                else -> db.getMemoDao().getMemosByColorIdAsc(colorId)
            }


            val imageMap = getImageMap(filteredMemos)
            withContext(Dispatchers.Main) {
                setRecyclerView(filteredMemos, imageMap)
            }
        }
    }

    private fun setRecyclerView(memoList: List<MemoEntity>, imageMap: Map<Int, List<PhotoEntity>>) {
        // setRecyclerView: 리사이클러뷰의 어댑터와 레이아웃 매니저를 설정하고 데이터를 바인딩합니다.
        requireActivity().runOnUiThread {
            val adapter = TagMemoRecyclerViewAdapter(ArrayList(memoList), imageMap, this)
            binding.tagRecyclerView.adapter = adapter
            binding.tagRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        }
    }

    private suspend fun getImageMap(memos: List<MemoEntity>): Map<Int, List<PhotoEntity>> {
        // 메모 리스트를 바탕으로 각 메모 ID에 해당하는 사진 리스트의 맵을 생성
        return memos.mapNotNull { memo ->
            memo.memo_id?.let { id ->
                val photos = photoDao.getPhotoByMemoId(id)
                id to photos
            }
        }.toMap()
    }


}