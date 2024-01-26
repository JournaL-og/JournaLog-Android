package com.jinin4.journalog.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jinin4.journalog.Preference
import com.jinin4.journalog.R
import com.jinin4.journalog.dataStore
import com.jinin4.journalog.databinding.FragmentDisplayPreferenceBinding
import com.jinin4.journalog.utils.FontUtils
import kotlinx.coroutines.launch

//반정현 - 24.01.22
class DisplayPreferenceFragment : Fragment() {
    private lateinit var binding: FragmentDisplayPreferenceBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDisplayPreferenceBinding.inflate(inflater, container, false)
        hideBottomNavigation(true)

        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.day_of_week_entries, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.weekStartDay.adapter = adapter


        // DataStore에서 값을 읽어와서 Spinner의 선택된 항목을 설정
        lifecycleScope.launch {
            context?.dataStore?.data?.collect { preferences ->
                val weekStartDay = preferences.weekStartDay
                val position = when (weekStartDay) {
                    Preference.DayOfWeek.SUNDAY -> 0
                    Preference.DayOfWeek.MONDAY -> 1
                    else -> 0
                }
                binding.weekStartDay.setSelection(position)
            }
        }

        // 주 시작 일 설정
        binding.weekStartDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDay = when (position) {
                    0 -> Preference.DayOfWeek.SUNDAY
                    1 -> Preference.DayOfWeek.MONDAY
                    else -> Preference.DayOfWeek.SUNDAY
                }

                lifecycleScope.launch {
                    context?.dataStore?.updateData { preferences ->
                        preferences.toBuilder().setWeekStartDay(selectedDay).build()
                    }
                }
            }
        }

        // DataStore에서 값을 읽어와서 스위치의 체크 상태를 설정
        lifecycleScope.launch {
            context?.dataStore?.data?.collect { preferences ->
                binding.calendarView.isChecked = !(preferences.calendarView)
            }
        }

        // 캘린더 보기 설정
        binding.calendarView.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                context?.dataStore?.updateData { preferences ->
                    preferences.toBuilder().setCalendarView(!isChecked).build()
                }
            }
        }

        binding.backButton.setOnClickListener{
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction
                .replace(androidx.fragment.R.id.fragment_container_view_tag ,
                    SettingFragment()
                ).commit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            val typeface = FontUtils.getFontType(requireContext())
            FontUtils.applyFont(view, typeface,17f)
        }
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
