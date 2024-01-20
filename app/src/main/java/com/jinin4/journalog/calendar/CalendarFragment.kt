package com.jinin4.journalog.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jinin4.journalog.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.threeten.bp.format.DateTimeFormatter

//이상원 - 24.01.19
class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var adapter: CalendarMemoRecyclerViewAdapter

    private var isWeek = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =FragmentCalendarBinding.inflate(inflater, container, false)

        val calendarView: MaterialCalendarView = binding.calendarView
        calendarView.topbarVisible = false // topbar 삭제
        calendarView.state().edit()
            .setCalendarDisplayMode(CalendarMode.WEEKS).commit() // 주만 보이도록
        calendarView.setSelectedDate(CalendarDay.today()) //오늘 날짜 자동 선택


        val formatter = DateTimeFormatter.ofPattern("M월 dd일 (EE)")
        binding.tvSelectedDate.text=calendarView.selectedDate!!.date.format(formatter) // 초기 좌상단 날짜 설정

        calendarView.setOnDateChangedListener { widget, date, selected ->
            binding.tvSelectedDate.text = date.date.format(formatter) // 날짜 클릭 시 좌상단 날짜 변경
        }
        val con = context ?: ""


        // 다른 날짜에 빨간색 점 찍기
        val todayDecorator = EventDecorator(5f, Color.GRAY, setOf(CalendarDay.today()))
        calendarView.addDecorator(todayDecorator)


//        val otherDayDecorator = EventDecorator(5f, Color.BLUE, setOf(CalendarDay.today()))
//        calendarView.addDecorator(otherDayDecorator)
//        val eventDecorator = EventDecorator(Color.GRAY, setOf(CalendarDay.today()))
//        calendarView.addDecorator(eventDecorator)

        // 우상단 달력 버튼 클릭 시 달력 펴기 접기
        binding.btnCalendar.setOnClickListener {
            if (isWeek) {
                calendarView.state().edit()
                    .setCalendarDisplayMode(CalendarMode.MONTHS).commit()
                isWeek = false
            } else {
                calendarView.state().edit()
                    .setCalendarDisplayMode(CalendarMode.WEEKS).commit()
                isWeek = true
            }

        }

        getAllTodoList()
        return binding.root
    }


    private fun getAllTodoList() {
        Thread {
            setRecyclerView()
        }.start()
    }

    private fun setRecyclerView() {
        // 리사이클러뷰 설정
        requireActivity().runOnUiThread {
            adapter = CalendarMemoRecyclerViewAdapter() // ❷ 어댑터 객체 할당
            binding.recyclerView.adapter = adapter // 리사이클러뷰 어댑터로 위에서 만든 어댑터 설정
            binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context) // 레이아웃 매니저 설정
        }
    }


}