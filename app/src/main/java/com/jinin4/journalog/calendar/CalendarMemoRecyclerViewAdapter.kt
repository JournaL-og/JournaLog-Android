package com.jinin4.journalog.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jinin4.journalog.databinding.ItemCalendarMemoBinding
import com.jinin4.journalog.db.memo.MemoDao
import com.jinin4.journalog.db.memo.MemoEntity

// 이상원 - 24.01.19
class CalendarMemoRecyclerViewAdapter(
    private val memoList: List<MemoEntity>
): RecyclerView.Adapter<CalendarMemoRecyclerViewAdapter.MemoViewHolder>() {
    inner class MemoViewHolder(binding: ItemCalendarMemoBinding) : RecyclerView.ViewHolder(binding.root) {

        val timestamp = binding.tvTimestamp
        val content = binding.tvContent
        val imageGrid = binding.gridView
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding: ItemCalendarMemoBinding = ItemCalendarMemoBinding
                                    .inflate(LayoutInflater.from(parent.context), parent, false)
        return MemoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {

        val memoData = memoList[position]
        when (memoData.color_id) {
            // 컬러ID에 따라 오른쪽 부분 색상 변경하는 로직~!
        }
        holder.timestamp.text = memoData.timestamp
        holder.content.text = memoData.content
    }
}