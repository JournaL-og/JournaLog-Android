package com.jinin4.journalog.calendar



import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jinin4.journalog.utils.BaseFragment
import com.jinin4.journalog.Preference
import com.jinin4.journalog.R
import com.jinin4.journalog.dataStore
import com.jinin4.journalog.calendar.bottom_sheet.MemoCreateBottomSheet
import com.jinin4.journalog.databinding.FragmentCalendarBinding
import com.jinin4.journalog.db.memo.MemoDao
import com.jinin4.journalog.db.memo.MemoEntity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.developia.todolist.db.JournaLogDatabase
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

//이상원 - 24.01.19, 반정현 - 24.01.22 수정
class CalendarFragment : BaseFragment(),MemoInsertCallback {

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
        //캘린더 보기 버튼 활성화 여부 확인, 한 주의 시작 일요일, 월요일 여부 확인
        viewLifecycleOwner.lifecycleScope.launch {
            context?.dataStore?.data?.collect { preferences ->
                val calendarView = preferences.calendarView
                binding.calendarView.visibility = if (!calendarView) View.VISIBLE else View.GONE
                val weekStartDay = preferences.weekStartDay
                val firstDayOfWeek = when (weekStartDay) {
                    Preference.DayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
                    Preference.DayOfWeek.MONDAY -> DayOfWeek.MONDAY
                    else -> DayOfWeek.SUNDAY
                }
                binding.calendarView.state().edit()
                    .setFirstDayOfWeek(firstDayOfWeek)
                    .commit()
            }
        }

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
        dotCalendar()


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
            val modal = MemoCreateBottomSheet(calendarView.selectedDate!!,this)
            modal.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)
            modal.setTargetFragment(this, 1)
            modal.show(requireActivity().supportFragmentManager, MemoCreateBottomSheet.TAG)
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
            adapter = CalendarMemoRecyclerViewAdapter(memoList, true) // ❷ 어댑터 객체 할당
            binding.recyclerView.adapter = adapter // 리사이클러뷰 어댑터로 위에서 만든 어댑터 설정
            binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context) // 레이아웃 매니저 설정
        }
    }

    override fun onMemoInserted() {
        // 모달에서 성공적으로 메모를 추가했을 때의 처리
        getMemos(binding.calendarView.selectedDate!!)
        dotCalendar()
    }

//    fun dotCalendar() {
//        var decorator: EventDecorator? = null
//        var DateList : List<CalendarDay>? = null
//        Thread {
//            DateList =   memoDao.getDistinctCalendarDays()
//        }.start()
//
//        decorator = EventDecorator(5f, Color.GRAY, DateList ?: setOf())
//        binding.calendarView.addDecorator(decorator)
//    }

    fun dotCalendar() {
        var decorator: EventDecorator? = null

        lifecycleScope.launch(Dispatchers.IO) {
            val dateList = memoDao.getDistinctCalendarDays()
            withContext(Dispatchers.Main) {
                decorator = EventDecorator(5f, Color.GRAY, convertDateList(dateList))
                binding.calendarView.addDecorator(decorator)
            }
        }
    }

    private fun convertDateList(dateList: List<String>): Set<CalendarDay> {
        return dateList.map { CalendarDay.from(LocalDate.parse(it)) }.toSet()
    }

}