package com.jinin4.journalog.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.jinin4.journalog.databinding.FragmentThemePreviewBinding
import com.jinin4.journalog.utils.BaseFragment
import com.jinin4.journalog.utils.CustomFontCalendarDecorator
import com.jinin4.journalog.utils.FontUtils
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.launch
//반정현 수정 - 24.01.26
class ThemePreviewFragment :BaseFragment(){
    private lateinit var binding: FragmentThemePreviewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThemePreviewBinding.inflate(inflater, container, false)
        val calendarView: MaterialCalendarView = binding.previewCalendarView
        calendarView.topbarVisible = false // topbar 삭제
        viewLifecycleOwner.lifecycleScope.launch {
            val typeface = FontUtils.getFontType(requireContext())
            val fontSize = FontUtils.getFontSize(requireContext())
            calendarView.addDecorator(CustomFontCalendarDecorator(typeface, fontSize*2))
        }
        calendarView.selectedDate = CalendarDay.today()
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
}