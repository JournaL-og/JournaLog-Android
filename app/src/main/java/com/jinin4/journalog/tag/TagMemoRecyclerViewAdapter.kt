package com.jinin4.journalog.tag

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jinin4.journalog.MemoRightColorSetting
import com.jinin4.journalog.R
import com.jinin4.journalog.calendar.CalendarMemoRecyclerViewAdapter
import com.jinin4.journalog.databinding.FragmentTagBinding
import com.jinin4.journalog.databinding.TagMemoBinding
import com.jinin4.journalog.db.memo.MemoEntity
import com.jinin4.journalog.utils.FontUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//최성혁 - 24.01.22
class TagMemoRecyclerViewAdapter(private val memoList : ArrayList<MemoEntity>) : RecyclerView.Adapter<TagMemoRecyclerViewAdapter.TagMemoViewHolder>(){

    inner class TagMemoViewHolder(binding: TagMemoBinding) : RecyclerView.ViewHolder(binding.root){
        val root = binding.root
        val memoContent: TextView = binding.tagviewMemoContent
        val memoDate: TextView = binding.tagviewMemoDate
        val cltag_ = binding.clTag
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
        val layerDrawable = ContextCompat.getDrawable(holder.root.context, R.drawable.rounded_textview_background_content_fixcolor) as LayerDrawable
        val memoColor =  when (memo.color_id) {
            1 -> R.color.red_sw
            2 -> R.color.orange_sw
            3 -> R.color.yellow_sw
            4 -> R.color.green_sw
            5 -> R.color.blue_sw
            6 -> R.color.navi_sw
            7 -> R.color.purple_sw
            else -> R.color.red_sw
        }

        val context = holder.itemView.context

        GlobalScope.launch {
            val typeface = FontUtils.getFontType(context)
            val fontSize = FontUtils.getFontSize(context)
            withContext(Dispatchers.Main) {
                FontUtils.applyFont(holder.itemView, typeface,fontSize)
            }
        }
        holder.cltag_.background =
            MemoRightColorSetting.changeRightColor(
                ContextCompat.getColor(holder.root.context, memoColor),layerDrawable)
        holder.memoContent.text = memo.content
        holder.memoDate.text = memo.timestamp
    }
}