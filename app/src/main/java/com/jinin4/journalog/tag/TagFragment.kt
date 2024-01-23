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
//최성혁 - 수정 24.01.22
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
        // default 전체 데이터
        getAllMemoList()
        binding.entireBtn.setOnClickListener { getAllMemoList() }

        // getMemoByColorId
        binding.redBtn.setOnClickListener { filterMemosByColorId(1) }
        binding.orangeBtn.setOnClickListener { filterMemosByColorId(2) }
        binding.yellowBtn.setOnClickListener { filterMemosByColorId(3) }
        binding.greenBtn.setOnClickListener { filterMemosByColorId(4) }
        binding.blueBtn.setOnClickListener { filterMemosByColorId(5) }
        binding.indigoBtn.setOnClickListener { filterMemosByColorId(6) }
        binding.puppleBtn.setOnClickListener { filterMemosByColorId(7) }

        return binding.root
    }

    private fun getAllMemoList() {
        CoroutineScope(Dispatchers.IO).launch {
            val memos = memoDao.getAllMemo()
            withContext(Dispatchers.Main) {
                setRecyclerView(memos)
            }
        }
    }

    private fun filterMemosByColorId(colorId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val filteredMemos = memoDao.getMemosByColorId(colorId)
            withContext(Dispatchers.Main) {
                setRecyclerView(filteredMemos)
            }
        }
    }

    private fun setRecyclerView(memoList: List<MemoEntity>){
        adapter = TagMemoRecyclerViewAdapter(ArrayList(memoList))
        binding.tagRecyclerView.adapter = adapter
        binding.tagRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
    }


}