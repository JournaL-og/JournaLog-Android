package com.jinin4.journalog.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jinin4.journalog.R
import com.jinin4.journalog.databinding.FragmentCalendarBinding
import com.jinin4.journalog.db.memo.MemoDao
import com.jinin4.journalog.db.memo.MemoEntity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import net.developia.todolist.db.JournaLogDatabase
import org.threeten.bp.format.DateTimeFormatter

//이상원 - 24.01.19
class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var adapter: CalendarMemoRecyclerViewAdapter
    private lateinit var memoList: List<MemoEntity>
    lateinit var db: JournaLogDatabase
    lateinit var memoDao: MemoDao

    private var isWeek = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        val calendarView: MaterialCalendarView = binding.calendarView
        calendarView.topbarVisible = false // topbar 삭제

//        calendarView.setBackgroundColor(resources.getColor(R.color.note))
        val formatter_hangul = DateTimeFormatter.ofPattern("M월 dd일 (EE)")


        db = JournaLogDatabase.getInstance(binding.root.context)!!
        memoDao = db.getMemoDao()
        calendarView.setOnDateChangedListener { widget, date, selected ->
            binding.tvSelectedDate.text = date.date.format(formatter_hangul) // 날짜 클릭 시 좌상단 날짜 변경
//            memoList = memoDao.getMemoByTimestamp(date.date.format(formatter_datetime))
            getMemos(date)

        }

        calendarView.selectedDate = CalendarDay.today() // 오늘 날짜 선택하기
        getMemos(CalendarDay.today()) //오늘 메모 가져오기
        binding.tvSelectedDate.text=calendarView.selectedDate!!.date.format(formatter_hangul)// 초기 좌상단 날짜 설정
//        calendarView.setSelectedDate(CalendarDay.today())
//        calendarView.setDateSelected(CalendarDay.today() )

        // 날짜 아래에 점 찍기
        val todayDecorator = EventDecorator(5f, Color.GRAY, setOf(CalendarDay.today()))
        calendarView.addDecorator(todayDecorator)


//        val otherDayDecorator = EventDecorator(5f, Color.BLUE, setOf(CalendarDay.today()))
//        calendarView.addDecorator(otherDayDecorator)
//        val eventDecorator = EventDecorator(Color.GRAY, setOf(CalendarDay.today()))
//        calendarView.addDecorator(eventDecorator)

        // 우상단 달력 버튼 클릭 시 달력 펴기 접기
        // 현재 월의 시작일

        binding.btnCalendar.setOnClickListener {
            if (isWeek) {
                calendarView.state().edit()
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit()
                isWeek = false

            } else {
                calendarView.state().edit()
                    .setCalendarDisplayMode(CalendarMode.WEEKS).commit()
                isWeek = true
            }

        }

        binding.fabAddMemo.setOnClickListener{
            val modal = ModalBottomSheet(calendarView.selectedDate!!)
            modal.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)
            modal.show(requireActivity().supportFragmentManager, ModalBottomSheet.TAG)
        }


//        getMemos()
        return binding.root
    }


    private fun getMemos(date: CalendarDay) {
        Thread {
            val formatter_datetime = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            memoList = memoDao.getMemoByTimestamp(date.date.format(formatter_datetime))
            setRecyclerView()
        }.start()
    }

    private fun setRecyclerView() {
        // 리사이클러뷰 설정
        requireActivity().runOnUiThread {
            adapter = CalendarMemoRecyclerViewAdapter(memoList) // ❷ 어댑터 객체 할당
            binding.recyclerView.adapter = adapter // 리사이클러뷰 어댑터로 위에서 만든 어댑터 설정
            binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context) // 레이아웃 매니저 설정
        }
    }

//    override fun onResume() {
//        super.onResume()
//        getMemos(binding.calendarView.selectedDate!!)
//    }





}