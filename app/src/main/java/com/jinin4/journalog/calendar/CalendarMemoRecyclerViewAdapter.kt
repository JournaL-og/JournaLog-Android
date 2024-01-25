package com.jinin4.journalog.calendar

import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.jinin4.journalog.MemoRightColorSetting
import com.jinin4.journalog.MemoRightColorSetting.getColorById
import com.jinin4.journalog.R
import com.jinin4.journalog.RecyclerViewItemClickListener
import com.jinin4.journalog.calendar.bottom_sheet.MemoCreateBottomSheet
import com.jinin4.journalog.databinding.ItemCalendarMemoBinding
import com.jinin4.journalog.databinding.ItemCalendarMemoTextBinding
import com.jinin4.journalog.db.memo.MemoEntity
import com.jinin4.journalog.utils.FontUtils
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// 이상원 - 24.01.23, 반정현 수정 - 24.01.24
class CalendarMemoRecyclerViewAdapter(
    private val memoList: List<MemoEntity>,
    private val isOnlyText: Boolean,
    private val selectedDay: CalendarDay,
    private val calendarFragment: CalendarFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_TEXT = 1
        private const val VIEW_TYPE_IMAGE = 2
    }

    private lateinit var binding_: ViewBinding

    inner class MemoViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val timestamp = if (isOnlyText) (binding as ItemCalendarMemoTextBinding).tvTimestamp else (binding as ItemCalendarMemoBinding).tvTimestamp
        val content = if (isOnlyText) (binding as ItemCalendarMemoTextBinding).tvContent else (binding as ItemCalendarMemoBinding).tvContent
        val imageGrid = if (isOnlyText) null else (binding as ItemCalendarMemoBinding).gridView
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ViewBinding = when (viewType) {
            VIEW_TYPE_TEXT -> ItemCalendarMemoTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            VIEW_TYPE_IMAGE -> ItemCalendarMemoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            else -> throw IllegalArgumentException("Unsupported view type")
        }
        binding_ = binding
        return MemoViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isOnlyText) VIEW_TYPE_TEXT else VIEW_TYPE_IMAGE
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val memoData = memoList[position]
        val layerDrawable = ContextCompat.getDrawable((holder as MemoViewHolder).root.context, R.drawable.rounded_textview_background_content) as LayerDrawable
        val memoColor = getColorById(memoData.color_id)
        val context = holder.itemView.context

        GlobalScope.launch {
            val typeface = FontUtils.getFontType(context)
            val fontSize = FontUtils.getFontSize(context)
            withContext(Dispatchers.Main) {
                FontUtils.applyFont(holder.itemView, typeface,fontSize)
            }
        }

        holder.content.background =
            MemoRightColorSetting.changeRightColor(
                ContextCompat.getColor(holder.root.context, memoColor),layerDrawable)
        holder.timestamp.text = memoData.timestamp // 여기서 timestamp는 HH:mm형식이다
        holder.content.text = memoData.content

        holder.content.setOnClickListener{
//            Toast.makeText(binding_.root.context, "dd", Toast.LENGTH_SHORT).show()
            val modal = MemoCreateBottomSheet(selectedDay, calendarFragment, memoData)
            modal.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)
            modal.setTargetFragment(calendarFragment, 1)
            modal.show(calendarFragment.requireActivity().supportFragmentManager, MemoCreateBottomSheet.TAG)
        }


    }

    fun convertStringToCalendarDay(dateString: String): CalendarDay? {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return try {
            val dateTime = LocalDateTime.parse(dateString, formatter)
            CalendarDay.from(dateTime.year, dateTime.monthValue, dateTime.dayOfMonth)
        } catch (e: Exception) {
            e.printStackTrace()
            // 예외 처리: 날짜 문자열을 파싱하는 동안 오류 발생 시
            null
        }
    }

}
