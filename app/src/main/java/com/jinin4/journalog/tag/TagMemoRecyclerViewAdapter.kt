package com.jinin4.journalog.tag

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jinin4.journalog.R
import com.jinin4.journalog.databinding.FragmentTagBinding
import com.jinin4.journalog.databinding.TagMemoBinding
import com.jinin4.journalog.db.memo.MemoEntity
//최성혁 - 24.01.22
class TagMemoRecyclerViewAdapter(private val memoList : ArrayList<MemoEntity>) : RecyclerView.Adapter<TagMemoRecyclerViewAdapter.TagMemoViewHolder>(){

    inner class TagMemoViewHolder(binding: TagMemoBinding) : RecyclerView.ViewHolder(binding.root){
        val root = binding.root
        val memoContent: TextView = binding.tagviewMemoContent
        val memoDate: TextView = binding.tagviewMemoDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagMemoViewHolder {
        val binding: TagMemoBinding = TagMemoBinding
            .inflate(LayoutInflater.from(parent.context),parent,false)
        return TagMemoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    override fun onBindViewHolder(holder: TagMemoViewHolder, position: Int) {
        val memo = memoList[position]
        holder.memoContent.text = memo.content
        holder.memoDate.text = memo.timestamp
    }
}