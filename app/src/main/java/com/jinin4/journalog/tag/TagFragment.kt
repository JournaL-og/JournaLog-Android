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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.developia.todolist.db.JournaLogDatabase
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//이상원 - 24.01.19
//최성혁 - 24.01.22
class TagFragment : Fragment() {

    lateinit var db: JournaLogDatabase
    private lateinit var binding: FragmentTagBinding
    private lateinit var adapter: TagMemoRecyclerViewAdapter
    lateinit var memoList: ArrayList<MemoEntity>
    lateinit var memoDao: MemoDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTagBinding.inflate(inflater, container, false)
        db = JournaLogDatabase.getInstance(binding.root.context)!!
        memoDao = db.getMemoDao()

//        binding.btnCallDb.setOnClickListener {
//            insertMemo()
//        }

        getAllMemoList()
        return binding.root
    }

//    private fun insertMemo() {
//        val koreaTimeZone = ZoneId.of("Asia/Seoul")
//        val currentDateTime = LocalDateTime.now(koreaTimeZone)
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//        val formattedDateTime = currentDateTime.format(formatter)
//        var memoEntity = MemoEntity(null, "아 아주 나른한 오후네요", formattedDateTime, 1)
//
//        CoroutineScope(Dispatchers.IO).launch {
//            memoDao.insertMemo(memoEntity)
//            withContext(Dispatchers.Main) {
//                Toast.makeText(binding.root.context, "추가되었습니다.", Toast.LENGTH_SHORT).show()
//                getAllMemoList() // 새로운 데이터로 RecyclerView 업데이트
//            }
//        }
//    }

    private fun getAllMemoList() {
        CoroutineScope(Dispatchers.IO).launch {
            val memos = memoDao.getAllMemo()
            withContext(Dispatchers.Main) {
                setRecyclerView(memos)
            }
        }
    }

    private fun setRecyclerView(memoList: List<MemoEntity>){
        adapter = TagMemoRecyclerViewAdapter(ArrayList(memoList))
        binding.tagRecyclerView.adapter = adapter
        binding.tagRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
    }
}