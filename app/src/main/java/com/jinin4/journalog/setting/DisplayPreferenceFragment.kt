package com.jinin4.journalog.setting

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jinin4.journalog.Preference
import com.jinin4.journalog.R
import com.jinin4.journalog.dataStore
import kotlinx.coroutines.launch

//반정현 - 24.01.22
class DisplayPreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.display_preference, rootKey)

        val weekStartDayPreference = findPreference<ListPreference>("weekStartDay")
        val calendarViewPreference = findPreference<SwitchPreferenceCompat>("calendarView")
        weekStartDayPreference?.summary = weekStartDayPreference?.entry
        // 주 시작 일 설정
        weekStartDayPreference?.setOnPreferenceChangeListener { preference, newValue ->
            val selectedDay = when (newValue) {
                "SUNDAY" -> Preference.DayOfWeek.SUNDAY
                "MONDAY" -> Preference.DayOfWeek.MONDAY
                else -> Preference.DayOfWeek.SUNDAY
            }

            lifecycleScope.launch {
                context?.dataStore?.updateData { preferences ->
                    preferences.toBuilder().setWeekStartDay(selectedDay).build()
                }
            }
            val index = weekStartDayPreference?.findIndexOfValue(newValue.toString())
            preference.summary = weekStartDayPreference?.entries?.get(index ?: 0)
            true
        }
        // 캘린더 보기
        calendarViewPreference?.setOnPreferenceChangeListener { preference, newValue ->
            lifecycleScope.launch {
                context?.dataStore?.updateData { preferences ->
                    preferences.toBuilder().setCalendarView(!(newValue as Boolean)).build()
                }
            }

            true
        }
    }

    // 이 화면에서는 하단 네비게이션 바 비활성화
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        hideBottomNavigation(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView에 위쪽 패딩 추가
        val recyclerView = listView
        val padding = resources.getDimensionPixelSize(R.dimen.toolbar_height)
        recyclerView.setPadding(0, padding, 0, 0)
        recyclerView.clipToPadding = false
    }


    override fun onDestroyView() {
        super.onDestroyView()
        hideBottomNavigation(false)
    }

    private fun hideBottomNavigation(isHide: Boolean) {
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (isHide) {
            bottomNavigation?.visibility = View.GONE
        } else {
            bottomNavigation?.visibility = View.VISIBLE
        }
    }


}
