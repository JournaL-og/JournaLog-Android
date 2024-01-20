package com.jinin4.journalog.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jinin4.journalog.databinding.ItemCalendarMemoBinding

// 이상원 - 24.01.19
class CalendarMemoRecyclerViewAdapter : RecyclerView.Adapter<CalendarMemoRecyclerViewAdapter.MemoViewHolder>() {
    inner class MemoViewHolder(binding: ItemCalendarMemoBinding) : RecyclerView.ViewHolder(binding.root) {

        val tv_title = binding.tvTitle
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding: ItemCalendarMemoBinding = ItemCalendarMemoBinding
                                    .inflate(LayoutInflater.from(parent.context), parent, false)
        return MemoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
//        holder.tv_title.text = "하이하이 테스트입니다."
    }
}